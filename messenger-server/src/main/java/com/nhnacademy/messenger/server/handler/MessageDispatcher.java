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

package com.nhnacademy.messenger.server.handler;

import com.nhnacademy.messenger.common.domain.MessageRequest;
import com.nhnacademy.messenger.common.domain.MessageResponse;
import com.nhnacademy.messenger.common.domain.MessageType;
import com.nhnacademy.messenger.server.session.SessionManager;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Objects;

public class MessageDispatcher {

    private final Map<MessageType, Handler> handlerMap;

    public MessageDispatcher() {
        this.handlerMap = HandlerFactory.getHandler();
    }

    public MessageDispatcher(Map<MessageType, Handler> handlerMap) {
        this.handlerMap = handlerMap;
    }

    public MessageResponse dispatch(MessageRequest request) {
        MessageType messageType = request.getHeader().getType();
        Handler handler = handlerMap.get(messageType);

        if (Objects.isNull(handler)) {
            return createErrorResponse("COMMON.INVALID_TYPE", "유효하지 않는 Message Type입니다.");
        }

        if (messageType != MessageType.LOGIN) {
            String sessionId = request.getHeader().getSessionId();
            if (StringUtils.isBlank(sessionId) || Objects.isNull(SessionManager.getSession(sessionId))) {
                return createErrorResponse("AUTH.INVALID_SESSION", "유효하지 않은 세션입니다.");
            }
        }

        return handler.handle(request);
    }

    private MessageResponse createErrorResponse(String code, String message) {
        return new MessageResponse(
                new MessageResponse.ResponseHeader(
                        MessageType.ERROR,
                        LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).toString(),
                        false
                ),
                Map.of(
                        "code", code,
                        "message", message
                )
        );
    }

    private Handler getHandler(MessageType messageType) {
        return handlerMap.get(messageType);
    }
}
