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
import com.nhnacademy.messenger.client.observer.impl.ClientSessionObserver;
import com.nhnacademy.messenger.client.observer.impl.UIUpdateObserver;
import com.nhnacademy.messenger.client.runnable.ReceivedMessageClient;
import com.nhnacademy.messenger.client.subject.EventType;
import com.nhnacademy.messenger.client.subject.MessageSubject;
import com.nhnacademy.messenger.client.subject.Subject;
import com.nhnacademy.messenger.client.ui.ClientUI;
import com.nhnacademy.messenger.client.ui.impl.ConsoleUI;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class ClientMain {

    private static final ClientUI clientUI = new ConsoleUI();
    private static final String DEFAULT_SERVER_ADDRESS = "localhost";
    private static final int DEFAULT_PORT = 12345;

    public static void main(String[] args) {
        try {
            // 서버 접속 시도
            Socket socket = new Socket(DEFAULT_SERVER_ADDRESS, DEFAULT_PORT);

            if (socket.isConnected()) {
                System.out.printf("[%s:%d] 서버 연결에 성공했습니다.\n",
                        DEFAULT_SERVER_ADDRESS, DEFAULT_PORT);
                System.out.println("/help 명령어 입력시 모든 명령어 목록을 볼 수 있습니다.");
            }

            Subject subject = new MessageSubject();
            subject.register(EventType.RECV, new ClientSessionObserver(clientUI));
            subject.register(EventType.RECV, new UIUpdateObserver(clientUI));

            // 서버로부터 오는 메시지를 받는 스레드
            Thread receiverThread = new Thread(new ReceivedMessageClient(socket, subject, clientUI));
            receiverThread.start();

            // 사용자 입력을 받아 서버로 전송
            Scanner scanner = new Scanner(System.in);
            CommandFactory commandFactory = new CommandFactory();

            System.out.print("> ");

            while (true) {
                String input = scanner.nextLine();

                if (input.trim().isEmpty()) {
                    clientUI.displayMessage("입력값이 비어있습니다.");
                }

                String[] parts = input.split(" ");

                ClientCommand command = commandFactory.getCommand(parts[0]);

                if (command != null) {
                    // 명령어 싪행
                    command.execute(parts, socket.getOutputStream());
                } else {
                    clientUI.displayMessage("지원하지 않는 명령어입니다.");
                }

                System.out.print("> ");
            }

        } catch (IOException e) {
            clientUI.displayMessage(String.format("예상치 못한 오류: %s", e.getMessage()));
            throw new RuntimeException(e);
        }
    }

}
