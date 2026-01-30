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

import java.net.Socket;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.UUID;

public class LoginHandler implements Handler {
    private final UserRepository userRepository = new MemoryUserRepository();

    @Override
    public MessageResponse handle(MessageRequest request) {
        return null;
    }

    @Override
    public MessageResponse handleWithSocket(MessageRequest request, Socket socket) {
        String sessionId = UUID.randomUUID().toString();
        String userId = request.getData().get("userId").toString();
        SessionManager.addSession(new Session(sessionId, userId, socket));

        return new MessageResponse(
                new MessageResponse.ResponseHeader(
                        MessageType.LOGIN_SUCCESS,
                        LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).toString(),
                        true),
                Map.of(
                        "userId", userId,
                        "sessionId", sessionId,
                        "message", "welcome"
                )
        );
    }
}
