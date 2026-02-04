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
import com.nhnacademy.messenger.client.context.ClientContext;
import com.nhnacademy.messenger.client.observer.impl.ClientSessionObserver;
import com.nhnacademy.messenger.client.observer.impl.UIUpdateObserver;
import com.nhnacademy.messenger.client.runnable.ReceivedMessageClient;
import com.nhnacademy.messenger.client.session.ClientSession;
import com.nhnacademy.messenger.client.subject.EventType;
import com.nhnacademy.messenger.client.subject.MessageSubject;
import com.nhnacademy.messenger.client.subject.Subject;
import com.nhnacademy.messenger.client.ui.ClientUI;
import com.nhnacademy.messenger.client.ui.impl.ConsoleUI;
import com.nhnacademy.messenger.client.ui.impl.SwingUI;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class ClientMain {

    private static Socket socket;

    private static CommandFactory commandFactory;

    private static ClientUI clientUI;

    private static ClientSession clientSession;

    private static ClientContext clientContext;

    private static final String DEFAULT_SERVER_ADDRESS = "localhost";
    private static final int DEFAULT_PORT = 12345;

    public static void main(String[] args) {
        // 실행 방법
        // 콘솔 모드: java -jar client-shade.jar
        // GUI 모드: java -jar client-shade.jar gui
        boolean isGui = args.length > 0 && args[0].equalsIgnoreCase("gui");

        try {
            socket = new Socket(DEFAULT_SERVER_ADDRESS, DEFAULT_PORT);

            if (socket.isConnected()) {
                clientUI.displayMessage(String.format("%s:%d 서버 연결에 성공했습니다.\n",
                        DEFAULT_SERVER_ADDRESS, DEFAULT_PORT));
            }

            if (isGui) {
                clientUI = new SwingUI(ClientMain::processCommand);
            } else {
                clientUI = new ConsoleUI();

                clientUI.displayMessage("/help 명령어 입력시 모든 명령어 목록을 볼 수 있습니다.");
            }

            clientSession = new ClientSession();
            clientContext = new ClientContext(clientSession, clientUI, socket);

            Subject subject = new MessageSubject();

            subject.register(EventType.RECV, new ClientSessionObserver(clientUI, clientSession));
            subject.register(EventType.RECV, new UIUpdateObserver(clientUI));

            // 서버로부터 오는 메시지를 받는 스레드
            Thread receiverThread = new Thread(new ReceivedMessageClient(socket, subject, clientUI));
            receiverThread.start();

            commandFactory = new CommandFactory(clientUI);

            // 입력 로직 분기
            if (!isGui) {
                runConsoleLoop();
            }

        } catch (IOException e) {
            clientUI.displayMessage(String.format("예상치 못한 오류: %s", e.getMessage()));
        }
    }

    private static void runConsoleLoop() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("> ");

        while (true) {
            processCommand(scanner.nextLine());
        }
    }

    private static void processCommand(String input) {
        if (input == null || input.trim().isEmpty()) {
            return;
        }

        try {
            String[] parts = input.split(" ");
            ClientCommand<?> command = commandFactory.getCommand(parts[0]);

            if (command != null) {
                executeGenericCommand(command, parts);
            } else {
                clientUI.displayMessage("지원하지 않는 명령어입니다.");
            }

        } catch (IllegalArgumentException e) {
            clientUI.displayMessage(e.getMessage());

        } catch (Exception e) {
            clientUI.displayError(String.format("명령어 처리 중 오류 발생: %s", e.getMessage()));
        }
    }

    private static <T> void executeGenericCommand(ClientCommand<T> command, String[] args) {
        T params = command.parse(args);

        command.execute(params, clientContext);
    }

}
