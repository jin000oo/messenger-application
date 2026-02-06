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
import com.nhnacademy.messenger.common.domain.MessageType;
import com.nhnacademy.messenger.common.dto.request.HistoryRequest;
import com.nhnacademy.messenger.common.dto.response.HistoryResponse;
import com.nhnacademy.messenger.common.dto.response.info.MessageInfo;
import com.nhnacademy.messenger.server.handler.Handler;
import com.nhnacademy.messenger.server.handler.HandlerResult;
import com.nhnacademy.messenger.server.message.domain.ChatMessage;
import com.nhnacademy.messenger.server.message.repository.MessageRepository;
import com.nhnacademy.messenger.server.utils.ResponseFactory;
import lombok.RequiredArgsConstructor;

import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
public class ChatMessageHistoryHandler implements Handler {

    private final MessageRepository messageRepository;

    @Override
    public HandlerResult handle(MessageRequest<?> request) {
        if (request == null || request.getHeader() == null || request.getData() == null) {
            return ResponseFactory.error("COMMON.BAD_REQUEST", "데이터 형식이 올바르지 않습니다.");
        }

        if (!(request.getData() instanceof HistoryRequest data) || data.roomId() == null) {
            return ResponseFactory.error("COMMON.BAD_REQUEST", "데이터 형식이 올바르지 않습니다.");
        }

        int limit = (data.limit() == null || data.limit() <= 0) ? 50 : data.limit();
        long before = (data.beforeMessageId() == null) ? Long.MAX_VALUE : data.beforeMessageId();

        List<ChatMessage> chatMessages = messageRepository.findAll(data.roomId());
        if (chatMessages.isEmpty()) {
            return ResponseFactory.success(
                    MessageType.CHAT_MESSAGE_HISTORY_SUCCESS,
                    new HistoryResponse(Collections.emptyList(), false)
            );
        }

        List<ChatMessage> filteredMessage = chatMessages.stream()
                .filter(message -> message.getMessageId() < before)
                .toList();
        if (filteredMessage.isEmpty()) {
            return ResponseFactory.success(
                    MessageType.CHAT_MESSAGE_HISTORY_SUCCESS,
                    new HistoryResponse(Collections.emptyList(), false)
            );
        }

        boolean hasMore = filteredMessage.size() > limit;

        List<ChatMessage> truncated = filteredMessage.subList(Math.max(0, filteredMessage.size() - limit), filteredMessage.size());

        List<MessageInfo> messageHistory = truncated.stream()
                .map(message -> new MessageInfo(
                        message.getMessageId(),
                        message.getSenderId(),
                        message.getSenderName(),
                        message.getTimestamp(),
                        message.getContent()
                ))
                .toList();

        return ResponseFactory.success(
                MessageType.CHAT_MESSAGE_HISTORY_SUCCESS,
                new HistoryResponse(messageHistory, hasMore)
        );
    }
}
