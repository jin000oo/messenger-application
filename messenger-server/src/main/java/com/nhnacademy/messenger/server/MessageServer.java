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

package com.nhnacademy.messenger.server;

import com.nhnacademy.messenger.server.handler.MessageDispatcher;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

@Slf4j
public class MessageServer implements Runnable {

    private static final int SERVER_PORT = 12345;
    private final MessageDispatcher messageDispatcher = new MessageDispatcher();

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(SERVER_PORT)) {
            log.info("서버 시작 [port={}]", SERVER_PORT);

            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Socket client = serverSocket.accept();
                    log.info("[{}:{}] 클라이언트 접속", client.getInetAddress().getHostAddress(), client.getPort());

                    Thread clientThread = new Thread(new ClientHandler(client, messageDispatcher));
                    clientThread.start();
                } catch (IOException e) {
                    log.warn("클라이언트 연결 중 오류 발생", e);
                }
            }
        } catch (IOException e) {
            log.error("서버 실행 실패 [port={}]", SERVER_PORT, e);
        } finally {
            log.info("서버 종료 [port={}]", SERVER_PORT);
        }
    }
}
