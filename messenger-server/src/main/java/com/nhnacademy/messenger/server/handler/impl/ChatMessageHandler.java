/*
 * +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
 * + Copyright 2026. NHN Academy Corp. All rights reserved.
 * + * While every precaution has been taken in the preparation of this resource,  assumes no
 * + responsibility for errors or omissions, or for damages resulting from the use of the information
 * + contained herein
 * + No part of this resource may be reproduced, stored in a retrieval system, or transmitted, in any
 * + form or by any means, electronic, mechanical, photocopying, recording, or otherwise, without the
 * + prior written permission.
 * +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
 */

package com.nhnacademy.messenger.server.handler.impl;

import com.nhnacademy.messenger.common.domain.MessageRequest;
import com.nhnacademy.messenger.common.domain.MessageResponse;
import com.nhnacademy.messenger.common.domain.MessageType;
import com.nhnacademy.messenger.common.util.MessageUtils;
import com.nhnacademy.messenger.server.chatroom.chatroomrepository.impl.MemoryChatRoomRepository;
import com.nhnacademy.messenger.server.chatroom.domain.ChatRoom;
import com.nhnacademy.messenger.server.handler.Handler;
import com.nhnacademy.messenger.server.message.domain.ChatMessage;
import com.nhnacademy.messenger.server.message.repository.impl.MemoryMessageRepository;
import com.nhnacademy.messenger.server.session.Session;
import com.nhnacademy.messenger.server.session.SessionManager;
import com.nhnacademy.messenger.server.user.repository.UserRepository;
import com.nhnacademy.messenger.server.user.repository.impl.MemoryUserRepository;
import com.nhnacademy.messenger.server.utils.IdGenerator;
import com.nhnacademy.messenger.server.utils.ResponseFactory;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Slf4j
public class ChatMessageHandler implements Handler {

    private final UserRepository userRepository = new MemoryUserRepository();
    private final MemoryChatRoomRepository chatRoomRepository = new MemoryChatRoomRepository();
    private final MemoryMessageRepository messageRepository = new MemoryMessageRepository();

    @Override
    public MessageResponse handle(MessageRequest request) {
        if (Objects.isNull(request)) {
            return ResponseFactory.error("COMMON.BAD_REQUEST", "데이터 형식이 올바르지 않습니다.");
        }

        // 유효한 세션인지 다시 확인.
        String sessionId = request.getHeader().getSessionId();
        Session session = SessionManager.findBySessionId(sessionId);
        if (Objects.isNull(session)) {
            return ResponseFactory.error("AUTH.INVALID_SESSION", "유효하지 않은 세션입니다.");
        }

//        Long roomId = (Long) request.getData().get("roomId");
//        String message = (String) request.getData().get("message");

        Object obj1 = request.getData().get("roomId");
        Object obj2 = request.getData().get("message");

        if (obj1 == null || obj2 == null) {
            return ResponseFactory.error("COMMON.BAD_REQUEST", "데이터 형식이 올바르지 않습니다.");
        }
        long roomId = Long.parseLong(String.valueOf(obj1));
        String message = String.valueOf(obj2);

//        if (roomId == null || message == null) {
//            return ResponseFactory.error("COMMON.BAD_REQUEST", "데이터 형식이 올바르지 않습니다.");
//        }

        if (!chatRoomRepository.exists(roomId)) {
            return ResponseFactory.error("ROOM.NOT_FOUND", "해당 채팅방을 찾을 수 없습니다.");
        }

        ChatRoom chatRoom = chatRoomRepository.findById(roomId).get();
        Set<String> members = chatRoom.getAllMembers();

        String senderId = session.getUserId();
        MessageResponse response = ResponseFactory.success(
                MessageType.CHAT_MESSAGE,
                Map.of(
                        "senderId", senderId,
                        "message", message
                )
        );

        for (String userId : members) {
            if (userId.equals(senderId)) {
                continue;
            }

            try {
                MessageUtils.send(
                        SessionManager.findByUserId(userId).getSocket().getOutputStream(),
                        response
                );
            } catch (IOException e) {
                log.debug("메시지 전송 중 오류 발생: {}", e.getMessage());
            }
        }

        // 수정 필요.
        long messageId = IdGenerator.randomMessageIdGenerator();

        messageRepository.save(new ChatMessage(
                messageId,
                roomId,
                session.getUserId(),
                userRepository.find(session.getUserId()).get().getUserName(),
                Instant.now().truncatedTo(ChronoUnit.SECONDS).toString(),
                message
        ));

        return ResponseFactory.success(
                MessageType.CHAT_MESSAGE_SUCCESS,
                Map.of(
                        "roomId", roomId,
                        "messageId", messageId
                )
        );
    }
}
