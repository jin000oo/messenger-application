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
import com.nhnacademy.messenger.server.handler.Handler;
import com.nhnacademy.messenger.server.message.domain.ChatMessage;
import com.nhnacademy.messenger.server.message.repository.MessageRepository;
import com.nhnacademy.messenger.server.session.Session;
import com.nhnacademy.messenger.server.utils.ResponseFactory;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RequiredArgsConstructor
public class ChatMessageHistoryHandler implements Handler {

    private final MessageRepository messageRepository;

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

        Object obj1 = request.getData().get("roomId");
        Object obj2 = request.getData().get("limit");
        Object obj3 = request.getData().get("beforeMessageId");
        if (obj1 == null || obj2 == null || obj3 == null) {
            return ResponseFactory.error("COMMON.BAD_REQUEST", "데이터 형식이 올바르지 않습니다.");
        }

        long roomId;
        int limit;
        long beforeMessageId;
        try {
            roomId = Long.parseLong(String.valueOf(obj1));
            limit = Integer.parseInt(String.valueOf(obj2));
            beforeMessageId = Long.parseLong(String.valueOf(obj3));
        } catch (NumberFormatException e) {
            return ResponseFactory.error("COMMON.BAD_REQUEST", "데이터 형식이 올바르지 않습니다.");
        }

        List<ChatMessage> chatMessages = messageRepository.findAll(roomId, limit);
        List<Map<String, Object>> history = new ArrayList<>();

        for (ChatMessage chatMessage : chatMessages) {
            long messageId = chatMessage.getMessageId();
            if (messageId >= beforeMessageId) {
                break;
            }

            String senderId = chatMessage.getSenderId();
            String senderName = chatMessage.getSenderName();
            String timestamp = chatMessage.getTimestamp();
            String content = chatMessage.getContent();

            history.add(Map.of(
                    "messageId", messageId,
                    "senderId", senderId,
                    "senderName", senderName,
                    "timestamp", timestamp,
                    "content", content
            ));
        }

        return ResponseFactory.success(
                MessageType.CHAT_MESSAGE_HISTORY_SUCCESS,
                Map.of(
                        "roomId", roomId,
                        "messages", history
                )
        );
    }
}
