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
import com.nhnacademy.messenger.server.session.Session;
import com.nhnacademy.messenger.server.session.SessionManager;
import com.nhnacademy.messenger.server.user.domain.User;
import com.nhnacademy.messenger.server.user.repository.UserRepository;
import com.nhnacademy.messenger.server.utils.ResponseFactory;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.net.Socket;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
public class LoginHandler implements Handler {

    private final UserRepository userRepository;

    @Override
    public MessageResponse handle(MessageRequest request) {
        return null;
    }

    @Override
    public MessageResponse handleWithSocket(MessageRequest request, Socket socket) {
        if (Objects.isNull(request) || Objects.isNull(request.getData())) {
            return ResponseFactory.error("COMMON.BAD_REQUEST", "데이터 형식이 올바르지 않습니다.");
        }

        Object objUserId = request.getData().get("userId");
        Object objPassword = request.getData().get("password");

        if (Objects.isNull(objUserId) || Objects.isNull(objPassword)) {
            return ResponseFactory.error("COMMON.BAD_REQUEST", "데이터 형식이 올바르지 않습니다.");
        }

        String userId = String.valueOf(objUserId);
        String password = String.valueOf(objPassword);

        if (StringUtils.isBlank(userId) || StringUtils.isBlank(password)) {
            return ResponseFactory.error("COMMON.BAD_REQUEST", "데이터 형식이 올바르지 않습니다.");
        }

        // 아이디를 찾을 수 없는 경우.
        Optional<User> optionalUser = userRepository.find(userId);
        if (optionalUser.isEmpty()) {
            return ResponseFactory.error("AUTH.INVALID_CREDENTIALS", "아이디 또는 비밀번호가 올바르지 않습니다.");
        }

        // Online 이면 로그인 실패.
        User user = optionalUser.get();
        if (user.isOnline()) {
            return ResponseFactory.error("AUTH.ALREADY_LOGGED_IN", "이미 로그인 상태입니다.");
        }

        // 아이디와 비밀번호 불일치.
        if (!user.getPassword().equals(password)) {
            return ResponseFactory.error("AUTH.INVALID_CREDENTIALS", "아이디 또는 비밀번호가 올바르지 않습니다.");
        }

        String sessionId = UUID.randomUUID().toString();
        SessionManager.addSession(new Session(sessionId, userId, socket));
        userRepository.setOnline(userId, true);

        return ResponseFactory.success(
                MessageType.LOGIN_SUCCESS,
                Map.of(
                        "userId", userId,
                        "sessionId", sessionId,
                        "message", "Welcome!"
                )
        );
    }
}
