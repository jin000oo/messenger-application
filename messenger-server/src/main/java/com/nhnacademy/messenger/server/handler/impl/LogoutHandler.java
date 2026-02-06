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
import com.nhnacademy.messenger.common.dto.response.LogoutResponse;
import com.nhnacademy.messenger.server.handler.Handler;
import com.nhnacademy.messenger.server.handler.HandlerResult;
import com.nhnacademy.messenger.server.session.SessionService;
import com.nhnacademy.messenger.server.user.repository.UserRepository;
import com.nhnacademy.messenger.server.utils.ResponseFactory;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@RequiredArgsConstructor
public class LogoutHandler implements Handler {

    private final UserRepository userRepository;
    private final SessionService sessionService;

    @Override
    public HandlerResult handle(MessageRequest<?> request) {
        if (request == null || request.getHeader() == null) {
            return ResponseFactory.error("COMMON.BAD_REQUEST", "데이터 형식이 올바르지 않습니다.");
        }

        String sessionId = request.getHeader().getSessionId();
        if (StringUtils.isBlank(sessionId) || !sessionService.validateSession(sessionId)) {
            return ResponseFactory.error("AUTH.INVALID_SESSION", "유효하지 않은 세션입니다.");
        }

        sessionService.removeSession(sessionId);

        return ResponseFactory.success(
                MessageType.LOGOUT_SUCCESS,
                new LogoutResponse("로그아웃 되었습니다.")
        );
    }
}
