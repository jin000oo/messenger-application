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
import com.nhnacademy.messenger.common.dto.request.LoginRequest;
import com.nhnacademy.messenger.common.dto.response.LoginResponse;
import com.nhnacademy.messenger.server.handler.HandlerResult;
import com.nhnacademy.messenger.server.handler.SocketHandler;
import com.nhnacademy.messenger.server.session.Session;
import com.nhnacademy.messenger.server.session.SessionService;
import com.nhnacademy.messenger.server.user.domain.User;
import com.nhnacademy.messenger.server.user.repository.UserRepository;
import com.nhnacademy.messenger.server.utils.ResponseFactory;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.net.Socket;
import java.util.Optional;

@RequiredArgsConstructor
public class LoginHandler implements SocketHandler {

    private final UserRepository userRepository;
    private final SessionService sessionService;

    @Override
    public HandlerResult handle(MessageRequest<?> request, Socket socket) {
        if (request == null || request.getHeader() == null || request.getData() == null) {
            return ResponseFactory.error("COMMON.BAD_REQUEST", "데이터 형식이 올바르지 않습니다.");
        }

        if (!(request.getData() instanceof LoginRequest(String userId, String password))) {
            return ResponseFactory.error("COMMON.BAD_REQUEST", "데이터 형식이 올바르지 않습니다.");
        }

        if (StringUtils.isBlank(userId) || StringUtils.isBlank(password)) {
            return ResponseFactory.error("COMMON.BAD_REQUEST", "데이터 형식이 올바르지 않습니다.");
        }

        // 아이디로 사용자를 찾을 수 없는 경우.
        Optional<User> optUser = userRepository.find(userId);
        if (optUser.isEmpty()) {
            return ResponseFactory.error("AUTH.INVALID_CREDENTIALS", "아이디 또는 비밀번호가 올바르지 않습니다.");
        }

        // 비밀번호 불일치 하는 경우.
        User user = optUser.get();
        if (!user.getPassword().equals(password)) {
            return ResponseFactory.error("AUTH.INVALID_CREDENTIALS", "아이디 또는 비밀번호가 올바르지 않습니다.");
        }

        // 기존 세션이 있는 경우 삭제한다.
        sessionService.removeByUserId(userId);
        // 새로운 세션을 추가한다.
        Session session = sessionService.registerSession(userId, socket);

        return ResponseFactory.success(
                MessageType.LOGIN_SUCCESS,
                new LoginResponse(userId, session.getSessionId(), "Welcome!")
        );
    }
}
