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

package com.nhnacademy.messenger.client;

import com.nhnacademy.messenger.common.domain.MessageRequest;
import com.nhnacademy.messenger.common.domain.MessageResponse;
import com.nhnacademy.messenger.common.domain.MessageType;
import com.nhnacademy.messenger.common.util.MessageUtils;
import java.io.IOException;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Scanner;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ClientMainDemo {

    private static final String DEFAULT_SERVER_ADDRESS = "localhost";
    private static final int DEFAULT_PORT = 8080;

    public static void main(String[] args) {
        try {
            // 서버 접속 시도
            Socket socket = new Socket(DEFAULT_SERVER_ADDRESS, DEFAULT_PORT);

            if (socket.isConnected()) {
                System.out.printf("[ClientMain] 클라이언트 소켓 연결에 성공했습니다. (%s:%d)%s",
                        DEFAULT_SERVER_ADDRESS, DEFAULT_PORT, System.lineSeparator());
            }

            // 서버로부터 오는 메시지를 받는 스레드
            Thread receiverThread = new Thread(() -> receiveMessage(socket));
            receiverThread.start();

            // 사용자 입력을 받아 서버로 전송
            Scanner scanner = new Scanner(System.in);

            while (true) {
                String input = scanner.nextLine();

                if ("exit".equals(input)) {
                    socket.close();
                    break;
                }

                MessageRequest request = new MessageRequest(
                        new MessageRequest.RequestHeader(
                                MessageType.LOGIN, LocalDateTime.now().toString(), "test-session"),
                        Map.of("userId", "test", "input", input));

                MessageUtils.send(socket.getOutputStream(), request);
            }

        } catch (IOException e) {
            System.out.printf("[ClientMain] 예상치 못한 오류: %s%s", e.getMessage(), System.lineSeparator());
            throw new RuntimeException(e);
        }
    }

    private static void receiveMessage(Socket socket) {
        try {
            while (!socket.isClosed()) {
                MessageResponse response = MessageUtils.readResponse(socket.getInputStream());

                if (response == null) {
                    System.out.println("[ClientMain] 서버와의 연결이 끊어졌습니다.");
                    break;
                }

                System.out.printf("[서버 응답]: %s%s", response, System.lineSeparator());
            }

        } catch (IOException e) {
            System.out.println("[ClientMain] 수신 종료");
        }
    }

}
