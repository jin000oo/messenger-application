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
import com.nhnacademy.messenger.common.dto.request.EnterChatRoomRequest;
import com.nhnacademy.messenger.common.dto.response.EnterChatRoomResponse;
import com.nhnacademy.messenger.server.chatroom.chatroomrepository.ChatRoomRepository;
import com.nhnacademy.messenger.server.chatroom.domain.ChatRoom;
import com.nhnacademy.messenger.server.handler.Handler;
import com.nhnacademy.messenger.server.session.Session;
import com.nhnacademy.messenger.server.session.SessionRepository;
import com.nhnacademy.messenger.server.utils.ResponseFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class ChatRoomEnterHandler implements Handler {

    private final ChatRoomRepository chatRoomRepository;
    private final SessionRepository sessionRepository;

    @Override
    public MessageResponse<?> handle(MessageRequest<?> request) {
        if (request == null || request.getHeader() == null || request.getData() == null) {
            return ResponseFactory.error("COMMON.BAD_REQUEST", "데이터 형식이 올바르지 않습니다.");
        }

        if (!(request.getData() instanceof EnterChatRoomRequest(Long roomId)) || roomId == null) {
            return ResponseFactory.error("COMMON.BAD_REQUEST", "데이터 형식이 올바르지 않습니다.");
        }

        Session session = sessionRepository.getSession(request.getHeader().getSessionId());
        if (session == null) {
            return ResponseFactory.error("AUTH.INVALID_SESSION", "유효하지 않은 세션입니다.");
        }

        ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElse(null);
        if (chatRoom == null) {
            return ResponseFactory.error("ROOM.NOT_FOUND", "채팅방을 찾을 수 없습니다.");
        }

        // 채팅방에 사용자를 추가한다.
        chatRoom.addMember(session.getUserId());
        List<String> members = chatRoom.getAllMembers().stream().toList();

        return ResponseFactory.success(
                MessageType.CHAT_ROOM_ENTER_SUCCESS,
                new EnterChatRoomResponse(roomId, members)
        );
    }
}
