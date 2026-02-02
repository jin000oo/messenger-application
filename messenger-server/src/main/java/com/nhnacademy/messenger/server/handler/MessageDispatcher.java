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
import com.nhnacademy.messenger.server.session.Session;
import com.nhnacademy.messenger.server.session.SessionManager;
import com.nhnacademy.messenger.server.utils.HeaderValidator;
import com.nhnacademy.messenger.server.utils.ResponseFactory;

import java.net.Socket;
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

    public MessageResponse dispatch(MessageRequest request, Socket socket) {
        HeaderValidator.ValidationError validationError = HeaderValidator.validateHeader(request.getHeader());

        // 헤더 확인.
        if (validationError != null) {
            return ResponseFactory.error(validationError.getCode(), validationError.getMessage());
        }

        if (request.getData() == null) {
            return ResponseFactory.error("COMMON.BAD_REQUEST", "데이터 형식이 올바르지 않습니다.");
        }

        MessageType messageType = request.getHeader().getType();
        Handler handler = handlerMap.get(messageType);

        // 메시지 타입에 따른 핸들러 확인.
        if (Objects.isNull(handler)) {
            return ResponseFactory.error("COMMON.UNSUPPORTED_TYPE", "아직 지원하지 않는 메시지 타입입니다.");
        }

        // 로그인
        if (messageType == MessageType.LOGIN) {
            return handler.handleWithSocket(request, socket);
        }

        // 재접속 시 소켓 업데이트
        // + 소켓 업데이트 시 user Online true로 변경.
        Session session = SessionManager.findBySessionId(request.getHeader().getSessionId());
        if (session.getSocket() != socket) {
            session.setSocket(socket);
        }

        return handler.handle(request);
    }
}
