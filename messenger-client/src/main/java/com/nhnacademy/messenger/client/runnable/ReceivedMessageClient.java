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

package com.nhnacademy.messenger.client.runnable;

import com.nhnacademy.messenger.client.session.ClientSession;
import com.nhnacademy.messenger.client.subject.Subject;
import com.nhnacademy.messenger.common.domain.MessageResponse;
import com.nhnacademy.messenger.common.domain.MessageType;
import com.nhnacademy.messenger.common.util.MessageUtils;
import java.io.IOException;
import java.net.Socket;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ReceivedMessageClient implements Runnable {

    private final Socket socket;
    private final Subject subject;

    public ReceivedMessageClient(Socket socket, Subject subject) {
        if (socket == null) {
            throw new IllegalArgumentException("[ReceivedMessageClient] Socket이 null입니다.");
        }

        if (subject == null) {
            throw new IllegalArgumentException("[ReceivedMessageClient] Subject가 null입니다.");
        }

        this.socket = socket;
        this.subject = subject;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                MessageResponse response = MessageUtils.readResponse(socket.getInputStream());

                if (response == null) {
                    break;
                }

                if (response.getHeader() != null) {
                    // 로그인 성공 시 세션 ID 저장
                    if (response.getHeader().getType() == MessageType.LOGIN_SUCCESS) {
                        Map<String, Object> data = response.getData();

                        if (data != null && data.containsKey("sessionId")) {
                            String sessionId = (String) data.get("sessionId");
                            String userId = (String) data.get("userId");

                            ClientSession.setSessionId(sessionId);
                            ClientSession.setUserId(userId);
                        }

                        // 로그아웃 성공 시 세션 ID 지우기
                    } else if (response.getHeader().getType() == MessageType.LOGOUT_SUCCESS) {
                        ClientSession.setSessionId(null);
                        ClientSession.setUserId(null);

                        System.out.println("[Client] 로그아웃 성공");
                    }
                }

                subject.receiveMessage(String.valueOf(response));

            } catch (IOException e) {
                System.out.printf("[Client] 예상치 못한 오류: %s%s", e.getMessage(), System.lineSeparator());
                throw new RuntimeException(e);
            }
        }
    }

}
