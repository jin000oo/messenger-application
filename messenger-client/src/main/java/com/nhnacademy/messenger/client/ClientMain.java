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

import com.nhnacademy.messenger.client.command.ClientCommand;
import com.nhnacademy.messenger.client.command.CommandFactory;
import com.nhnacademy.messenger.client.command.impl.ChatCommand;
import com.nhnacademy.messenger.client.observer.console.ConsoleRecvObserver;
import com.nhnacademy.messenger.client.runnable.ReceivedMessageClient;
import com.nhnacademy.messenger.client.subject.EventType;
import com.nhnacademy.messenger.client.subject.MessageSubject;
import com.nhnacademy.messenger.client.subject.Subject;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class ClientMain {

    private static final String DEFAULT_SERVER_ADDRESS = "localhost";
    private static final int DEFAULT_PORT = 8080;

    public static void main(String[] args) {
        try {
            // 서버 접속 시도
            Socket socket = new Socket(DEFAULT_SERVER_ADDRESS, DEFAULT_PORT);

            if (socket.isConnected()) {
                System.out.printf("[ClientMain] 서버 연결 성공 (%s:%d)%s",
                        DEFAULT_SERVER_ADDRESS, DEFAULT_PORT, System.lineSeparator());
            }

            Subject subject = new MessageSubject();
            subject.register(EventType.RECV, new ConsoleRecvObserver());

            // 서버로부터 오는 메시지를 받는 스레드
            Thread receiverThread = new Thread(new ReceivedMessageClient(socket, subject));
            receiverThread.start();

            // 사용자 입력을 받아 서버로 전송
            Scanner scanner = new Scanner(System.in);
            CommandFactory commandFactory = new CommandFactory();

            System.out.println("명령어를 입력하세요!");
            System.out.print("> ");

            while (true) {
                String input = scanner.nextLine();

                if (input.trim().isEmpty()) {
                    continue;
                }

                String[] parts = input.split(" ");

                ClientCommand command = commandFactory.getCommand(parts[0]);

                if (command != null) {
                    // 명령어 싪행
                    command.execute(parts, socket.getOutputStream());
                } else {
                    // 명령어가 아니면 일반 채팅으로 간주
                    new ChatCommand().execute(parts, socket.getOutputStream());
                }

                System.out.print("> ");
            }

        } catch (IOException e) {
            System.out.printf("[ClientMain] 예상치 못한 오류: %s%s", e.getMessage(), System.lineSeparator());
            throw new RuntimeException(e);
        }
    }

}
