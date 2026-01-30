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
import com.nhnacademy.messenger.server.user.repository.UserRepository;
import com.nhnacademy.messenger.server.user.repository.impl.MemoryUserRepository;
import com.nhnacademy.messenger.server.utils.ResponseFactory;

import java.util.Map;
import java.util.Objects;

public class LogoutHandler implements Handler {

    private final UserRepository userRepository = new MemoryUserRepository();

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

        String userId = session.getUserId();

        // 로그아웃 과정.
        userRepository.setOnline(userId, false);
        SessionManager.removeBySessionId(sessionId);

        return ResponseFactory.success(
                MessageType.LOGOUT_SUCCESS,
                Map.of(
                        "message", "로그아웃 되었습니다."
                )
        );
    }
}
