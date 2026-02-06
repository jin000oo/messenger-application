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
import com.nhnacademy.messenger.common.dto.request.WhisperRequest;
import com.nhnacademy.messenger.common.dto.response.WhisperResponse;
import com.nhnacademy.messenger.server.handler.Handler;
import com.nhnacademy.messenger.server.handler.HandlerResult;
import com.nhnacademy.messenger.server.message.domain.PrivateMessage;
import com.nhnacademy.messenger.server.message.repository.PrivateMessageRepository;
import com.nhnacademy.messenger.server.session.Session;
import com.nhnacademy.messenger.server.session.SessionRepository;
import com.nhnacademy.messenger.server.thread.MessageSender;
import com.nhnacademy.messenger.server.user.repository.UserRepository;
import com.nhnacademy.messenger.server.utils.IdGenerator;
import com.nhnacademy.messenger.server.utils.ResponseFactory;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Slf4j
@AllArgsConstructor
public class PrivateMessageHandler implements Handler {

    private final UserRepository userRepository;
    private final PrivateMessageRepository privateMessageRepository;
    private final SessionRepository sessionRepository;
    private final MessageSender sender;

    @Override
    public HandlerResult handle(MessageRequest<?> request) {
        if (request == null || request.getHeader() == null || request.getData() == null) {
            return ResponseFactory.error("COMMON.BAD_REQUEST", "데이터 형식이 올바르지 않습니다.");
        }

        if (!(request.getData() instanceof WhisperRequest(String senderId, String receiverId, String message))) {
            return ResponseFactory.error("COMMON.BAD_REQUEST", "데이터 형식이 올바르지 않습니다.");
        }

        if (StringUtils.isBlank(receiverId) || StringUtils.isBlank(message)) {
            return ResponseFactory.error("COMMON.BAD_REQUEST", "데이터 형식이 올바르지 않습니다.");
        }

        Session session = sessionRepository.getSession(request.getHeader().getSessionId());
        if (session == null) {
            return ResponseFactory.error("AUTH.INVALID_SESSION", "유효하지 않은 세션입니다.");
        }

        senderId = session.getUserId();
        if (!userRepository.exists(receiverId)) {
            return ResponseFactory.error("USER.NOT_FOUND", "수신자를 찾을 수 없습니다.");
        }

        long messageId = IdGenerator.nextMessageId();
        String now = Instant.now().truncatedTo(ChronoUnit.SECONDS).toString();
        privateMessageRepository.save(new PrivateMessage(
                messageId,
                senderId,
                receiverId,
                now,
                message
        ));

        MessageResponse<WhisperResponse> response = ResponseFactory.successResponse(
                MessageType.PRIVATE_MESSAGE,
                new WhisperResponse(senderId, receiverId, message, messageId)
        );

        sender.sendToUser(receiverId, response);

        return ResponseFactory.success(
                MessageType.PRIVATE_MESSAGE_SUCCESS,
                new WhisperResponse(senderId, receiverId, "귓속말이 전송되었습니다.", messageId)
        );
    }
}
