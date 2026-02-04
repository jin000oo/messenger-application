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
import com.nhnacademy.messenger.server.session.SessionService;
import com.nhnacademy.messenger.server.utils.HeaderValidator;
import com.nhnacademy.messenger.server.utils.ResponseFactory;
import lombok.RequiredArgsConstructor;

import java.net.Socket;

@RequiredArgsConstructor
public class MessageDispatcher {

    private final HandlerFactory handlerFactory;
    private final SessionService sessionService;

    public MessageResponse dispatch(MessageRequest request, Socket socket) {
        HeaderValidator.ValidationError error = HeaderValidator.validateHeader(request.getHeader());

        // request 확인
        if (error != null) {
            return ResponseFactory.error(error.getCode(), error.getMessage());
        }
        if (request.getData() == null) {
            return ResponseFactory.error("COMMON.BAD_REQUEST", "데이터 형식이 올바르지 않습니다.");
        }

        MessageType messageType = request.getHeader().getType();
        Handler handler = handlerFactory.getHandler(messageType);

        // 로그인
        if (handler instanceof SocketHandler socketHandler) {
            return socketHandler.handle(request, socket);
        }

        // 세션 유효 확인
        String sessionId = request.getHeader().getSessionId();
        if (!sessionService.validateSession(sessionId)) {
            return ResponseFactory.error("AUTH.INVALID_SESSION", "유효하지 않은 세션입니다.");
        }

        // 소켓 업데이트
        sessionService.reconnect(sessionId, socket);

        return handler.handle(request);
    }
}
