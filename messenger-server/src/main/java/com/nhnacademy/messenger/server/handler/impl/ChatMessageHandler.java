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

import com.nhnacademy.messenger.common.domain.ContentType;
import com.nhnacademy.messenger.common.domain.MessageRequest;
import com.nhnacademy.messenger.common.domain.MessageResponse;
import com.nhnacademy.messenger.common.domain.MessageType;
import com.nhnacademy.messenger.common.dto.request.ChatRequest;
import com.nhnacademy.messenger.common.dto.response.ChatResponse;
import com.nhnacademy.messenger.server.chatroom.chatroomrepository.ChatRoomRepository;
import com.nhnacademy.messenger.server.chatroom.domain.ChatRoom;
import com.nhnacademy.messenger.server.handler.Handler;
import com.nhnacademy.messenger.server.handler.HandlerResult;
import com.nhnacademy.messenger.server.message.domain.ChatMessage;
import com.nhnacademy.messenger.server.message.repository.MessageRepository;
import com.nhnacademy.messenger.server.notification.NotificationService;
import com.nhnacademy.messenger.server.session.Session;
import com.nhnacademy.messenger.server.session.SessionRepository;
import com.nhnacademy.messenger.server.thread.MessageSender;
import com.nhnacademy.messenger.server.user.repository.UserRepository;
import com.nhnacademy.messenger.server.utils.IdGenerator;
import com.nhnacademy.messenger.server.utils.ResponseFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
public class ChatMessageHandler implements Handler {

    private final UserRepository userRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final MessageRepository messageRepository;
    private final SessionRepository sessionRepository;
    private final MessageSender sender;
    private final NotificationService notificationService;

    @Override
    public HandlerResult handle(MessageRequest<?> request) {
        if (request == null || request.getHeader() == null || request.getData() == null) {
            return ResponseFactory.error("COMMON.BAD_REQUEST", "데이터 형식이 올바르지 않습니다.");
        }

        if (!(request.getData() instanceof ChatRequest(Long roomId, String message)) || roomId == null) {
            return ResponseFactory.error("COMMON.BAD_REQUEST", "데이터 형식이 올바르지 않습니다.");
        }

        if (StringUtils.isBlank(message)) {
            return ResponseFactory.error("COMMON.BAD_REQUEST", "데이터 형식이 올바르지 않습니다.");
        }

        Session session = sessionRepository.getSession(request.getHeader().getSessionId());
        if (session == null) {
            return ResponseFactory.error("AUTH.INVALID_SESSION", "유효하지 않은 세션입니다.");
        }

        ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElse(null);
        if (chatRoom == null) {
            return ResponseFactory.error("ROOM.NOT_FOUND", "해당 채팅방을 찾을 수 없습니다.");
        }

        long messageId = IdGenerator.nextMessageId();
        String senderId = session.getUserId();
        String senderName = userRepository.find(senderId)
                .map(user -> Objects.toString(user.getUserName(), ""))
                .orElse("");
        String now = Instant.now().truncatedTo(ChronoUnit.SECONDS).toString();

        messageRepository.save(new ChatMessage(
                messageId,
                roomId,
                senderId,
                senderName,
                now,
                message
        ));

        List<String> members = chatRoom.getAllMembers().stream().toList();
        MessageResponse<com.nhnacademy.messenger.common.dto.ChatMessage> response = ResponseFactory.successResponse(
                MessageType.CHAT_MESSAGE,
                new com.nhnacademy.messenger.common.dto.ChatMessage(senderId, message)
        );

        sender.sendToUsers(members, response);

        return ResponseFactory.success(
                MessageType.CHAT_MESSAGE_SUCCESS,
                new ChatResponse(roomId, messageId)
        ).addNotification(() -> notificationService.pushNewMessage(roomId, messageId, senderId, message, ContentType.TEXT, null, 0L));
    }
}
