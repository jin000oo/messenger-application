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
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

public class HeaderValidator {

    public static ValidationError validateHeader(MessageRequest.RequestHeader requestHeader) {
        // RequestHeader null 확인.
        if (requestHeader == null) {
            return new ValidationError("COMMON.BAD_REQUEST", "요청 헤더가 비어있습니다.");
        }

        // Message Type null 확인.
        // Message Type에 따른 Handler 확인은  MessageDispatcher에서.
        if (requestHeader.getType() == null) {
            return new ValidationError("COMMON.BAD_REQUEST", "메시지 타입 형식이 올바르지 않습니다.");
        } else if (requestHeader.getType() == MessageType.LOGIN) {
            return validateLoginHeader(requestHeader);
        }

        // timestamp null 확인.
        if (StringUtils.isBlank(requestHeader.getTimestamp())) {
            return new ValidationError("COMMON.BAD_REQUEST", "타임스탬프 형식이 올바르지 않습니다.");
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
