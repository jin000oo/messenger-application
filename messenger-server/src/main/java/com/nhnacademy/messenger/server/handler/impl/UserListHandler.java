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

import java.util.List;
import java.util.Map;
import java.util.Objects;

@RequiredArgsConstructor
public class UserListHandler implements Handler {

    private final UserRepository userRepository;

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

        // 유저 목록.
        List<Map<String, Object>> users = userRepository.findAll().stream()
                .filter(User::isOnline) // Online 상태만
                .map(user -> Map.<String, Object>of(
                        "id", user.getUserId(),
                        "name", Objects.toString(user.getUserName(), ""),
                        "online", user.isOnline()
                ))
                .toList();

        return ResponseFactory.success(
                MessageType.USER_LIST_SUCCESS,
                Map.of(
                        "users", users
                )
        );
    }
}
