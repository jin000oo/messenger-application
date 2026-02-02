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
import com.nhnacademy.messenger.server.handler.Handler;
import com.nhnacademy.messenger.server.message.domain.PrivateMessage;
import com.nhnacademy.messenger.server.message.repository.PrivateMessageRepository;
import com.nhnacademy.messenger.server.message.repository.impl.MemoryPrivateMessageRepository;
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

@Slf4j
public class PrivateMessageHandler implements Handler {

    private final UserRepository userRepository = new MemoryUserRepository();
    private final PrivateMessageRepository privateMessageRepository = new MemoryPrivateMessageRepository();

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

        String senderId = (String) request.getData().get("senderId");
        String receiverId = (String) request.getData().get("receiverId");
        String message = (String) request.getData().get("message");

        if (senderId == null || receiverId == null || message == null) {
            return ResponseFactory.error("COMMON.BAD_REQUEST", "데이터 형식이 올바르지 않습니다.");
        }

        if (userRepository.exists(receiverId)) {
            return ResponseFactory.error("USER.NOT_FOUND", "수신자를 찾을 수 없습니다.");
        }

        try {
            MessageUtils.send(
                    SessionManager.findByUserId(receiverId).getSocket().getOutputStream(),
                    message
            );
        } catch (IOException e) {
            log.debug("귓속말 메시지 전송 중 오류 발생: {}", e.getMessage());
        }

        long messageId = IdGenerator.randomMessageIdGenerator();
        privateMessageRepository.save(new PrivateMessage(
                messageId,
                senderId,
                receiverId,
                Instant.now().truncatedTo(ChronoUnit.SECONDS).toString(),
                message
        ));

        return ResponseFactory.success(
                MessageType.PRIVATE_MESSAGE_SUCCESS,
                Map.of(
                        "senderId", senderId,
                        "receiverId", receiverId,
                        "message", "귓속말이 전송되었습니다.",
                        "messageId", messageId
                )
        );
    }
}
