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

package com.nhnacademy.messenger.server.utils;

import com.nhnacademy.messenger.common.domain.MessageRequest;
import com.nhnacademy.messenger.common.domain.MessageType;
import com.nhnacademy.messenger.server.session.Session;
import com.nhnacademy.messenger.server.session.SessionManager;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

public class HeaderValidator {

    public static ValidationError validateHeader(MessageRequest.RequestHeader requestHeader) {
        // RequestHeader null 확인.
        if (Objects.isNull(requestHeader)) {
            return new ValidationError("COMMON.BAD_REQUEST", "요청 헤더가 비어있습니다.");
        }

        // Message Type null 확인.
        // Message Type에 따른 Handler 확인은  MessageDispatcher에서.
        if (Objects.isNull(requestHeader.getType())) {
            return new ValidationError("COMMON.BAD_REQUEST", "메시지 타입 형식이 올바르지 않습니다.");
        } else if (requestHeader.getType() == MessageType.LOGIN) {
            return validateLoginHeader(requestHeader);
        }

        // timestamp null 확인.
        if (StringUtils.isBlank(requestHeader.getTimestamp())) {
            return new ValidationError("COMMON.BAD_REQUEST", "타임스탬프 형식이 올바르지 않습니다.");
        }

        // sessionId null 확인과 유효한 세션 확인.
        String sessionId = requestHeader.getSessionId();
        if (StringUtils.isBlank(sessionId)) {
            return new ValidationError("AUTH.INVALID_SESSION", "유효하지 않은 세션입니다.");
        }
        Session session = SessionManager.findBySessionId(sessionId);
        if (Objects.isNull(session)) {
            return new ValidationError("AUTH.INVALID_SESSION", "유효하지 않은 세션입니다.");
        }

        return null;
    }

    public static ValidationError validateLoginHeader(MessageRequest.RequestHeader requestHeader) {
        // timestamp null 확인.
        if (StringUtils.isBlank(requestHeader.getTimestamp())) {
            return new ValidationError("COMMON.BAD_REQUEST", "타임스탬프 형식이 올바르지 않습니다.");
        }

        // sessionId null인지 확인.
        String sessionId = requestHeader.getSessionId();
        if (!StringUtils.isBlank(sessionId)) {
            return new ValidationError("COMMON.BAD_REQUEST", "세션 아이디 형식이 올바르지 않습니다.");
        }

        return null;
    }

    @AllArgsConstructor
    @Getter
    public static class ValidationError {
        private final String code;
        private final String message;
    }
}
