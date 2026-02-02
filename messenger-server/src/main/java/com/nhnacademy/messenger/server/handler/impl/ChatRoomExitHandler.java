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
import com.nhnacademy.messenger.server.chatroom.chatroomrepository.impl.MemoryChatRoomRepository;
import com.nhnacademy.messenger.server.handler.Handler;
import com.nhnacademy.messenger.server.session.Session;
import com.nhnacademy.messenger.server.session.SessionManager;
import com.nhnacademy.messenger.server.utils.ResponseFactory;

import java.util.Map;
import java.util.Objects;

public class ChatRoomExitHandler implements Handler {

    private final MemoryChatRoomRepository chatRoomRepository = new MemoryChatRoomRepository();

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

        Object obj = request.getData().get("roomId");
        if (obj == null) {
            return ResponseFactory.error("COMMON.BAD_REQUEST", "데이터 형식이 올바르지 않습니다.");
        }
        long roomId = Long.parseLong(String.valueOf(obj));

        if (chatRoomRepository.findById(roomId).isEmpty()) {
            return ResponseFactory.error("ROOM.NOT_FOUND", "채팅방을 찾을 수 없습니다.");
        }

        chatRoomRepository.findById(roomId).get().removeMember(session.getUserId());

        return ResponseFactory.success(
                MessageType.CHAT_ROOM_EXIT_SUCCESS,
                Map.of(
                        "roomId", roomId,
                        "message", "채팅방에서 나갔습니다."
                )
        );
    }
}
