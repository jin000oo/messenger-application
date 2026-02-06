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

package com.nhnacademy.messenger.server.thread;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.messenger.common.domain.MessageResponse;
import com.nhnacademy.messenger.common.util.MessageUtils;
import com.nhnacademy.messenger.server.session.Session;
import com.nhnacademy.messenger.server.session.SessionRepository;
import com.nhnacademy.messenger.server.session.SessionService;
import com.nhnacademy.messenger.server.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class MessageSender {

    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;
    private final SessionService sessionService;
    private final MessageUtils messageUtils;

    private final static ObjectMapper objectMapper = new ObjectMapper();

    // 해당 Socket의 OutStream으로 전송한다.
    public void send(Socket socket, MessageResponse<?> response) {
        if (socket == null || response == null) {
            return;
        }

        synchronized (socket) {
            try {
                OutputStream out = socket.getOutputStream();
                String ip = socket.getInetAddress().getHostAddress();
                int port = socket.getPort();
                log.debug("[{}:{}] 응답: {}", ip, port, objectMapper.writeValueAsString(response));

                messageUtils.send(out, response);
            } catch (IOException e) {
                log.warn("전송 실패", e);
                closeSocket(socket);
                sessionService.removeBySocket(socket);
            }
        }
    }

    // 해당 UserId의 Socket을 SessionManager에서 찾아서 전송한다.
    public boolean sendToUser(String userId, MessageResponse<?> response) {
        // 해당 userId로 세션을 찾는다.
        Session session = sessionRepository.getByUserId(userId);
        // 세션이 없다면 전송 실패.
        if (session == null) return false;

        // 소켓이 없거나, 종료되었다면 전송 실패.
        Socket socket = session.getSocket();
        if (socket == null || socket.isClosed()) {
            closeSocket(session.getSocket());
            sessionService.removeByUserId(userId);
            return false;
        }

        send(socket, response);
        return true;
    }

    // 브로드캐스트.
    public void sendToUsers(List<String> userIds, MessageResponse<?> response) {
        for (String userId : userIds) {
            sendToUser(userId, response);
        }
    }

    private void closeSocket(Socket socket) {
        if (socket == null) return;

        try {
            socket.close();
        } catch (IOException e) {
        }
    }
}
