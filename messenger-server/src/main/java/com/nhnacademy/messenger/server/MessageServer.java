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

import com.nhnacademy.messenger.server.chatroom.chatroomrepository.ChatRoomRepository;
import com.nhnacademy.messenger.server.chatroom.chatroomrepository.impl.MemoryChatRoomRepository;
import com.nhnacademy.messenger.server.handler.HandlerFactory;
import com.nhnacademy.messenger.server.handler.MessageDispatcher;
import com.nhnacademy.messenger.server.message.repository.MessageRepository;
import com.nhnacademy.messenger.server.message.repository.PrivateMessageRepository;
import com.nhnacademy.messenger.server.message.repository.impl.MemoryMessageRepository;
import com.nhnacademy.messenger.server.message.repository.impl.MemoryPrivateMessageRepository;
import com.nhnacademy.messenger.server.session.AuthService;
import com.nhnacademy.messenger.server.session.SessionRepository;
import com.nhnacademy.messenger.server.session.SessionService;
import com.nhnacademy.messenger.server.session.TimeoutService;
import com.nhnacademy.messenger.server.session.impl.MemorySessionRepository;
import com.nhnacademy.messenger.server.thread.ClientHandler;
import com.nhnacademy.messenger.server.thread.MessageSender;
import com.nhnacademy.messenger.server.thread.channel.RequestChannel;
import com.nhnacademy.messenger.server.thread.pool.WorkerThreadPool;
import com.nhnacademy.messenger.server.user.repository.UserRepository;
import com.nhnacademy.messenger.server.user.repository.impl.MemoryUserRepository;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

@Slf4j
public class MessageServer implements Runnable {

    private static final int SERVER_PORT = 12345;

    private final HandlerFactory handlerFactory;
    private final MessageDispatcher messageDispatcher;
    private final RequestChannel requestChannel;
    private final WorkerThreadPool workerThreadPool;
    private final MessageSender messageSender;
    private final TimeoutService timeoutService;

    public MessageServer() {
        UserRepository userRepo = new MemoryUserRepository();
        ChatRoomRepository chatRoomRepo = new MemoryChatRoomRepository();
        MessageRepository messageRepo = new MemoryMessageRepository();
        PrivateMessageRepository privateMessageRepo = new MemoryPrivateMessageRepository();
        SessionRepository sessionRepo = new MemorySessionRepository();

        SessionService sessionService = new SessionService(userRepo, sessionRepo);
        AuthService authService = new AuthService(userRepo, sessionService);

        messageSender = new MessageSender(userRepo, sessionRepo, sessionService);
        handlerFactory = new HandlerFactory(userRepo, chatRoomRepo, messageRepo, privateMessageRepo, sessionRepo, authService, messageSender);
        messageDispatcher = new MessageDispatcher(handlerFactory, sessionService);
        requestChannel = new RequestChannel(16);
        workerThreadPool = new WorkerThreadPool(4, requestChannel);
        timeoutService = new TimeoutService(sessionRepo, sessionService, messageSender, 30_000, 120_000);
    }

    @Override
    public void run() {
        workerThreadPool.start();
        timeoutService.start();

        try (ServerSocket serverSocket = new ServerSocket(SERVER_PORT)) {
            log.info("서버 시작 [port={}]", SERVER_PORT);

            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Socket client = serverSocket.accept();
                    log.info("[{}:{}] 클라이언트 접속", client.getInetAddress().getHostAddress(), client.getPort());

                    new Thread(new ClientHandler(client, requestChannel, messageDispatcher, messageSender)).start();
                } catch (IOException e) {
                    log.warn("클라이언트 연결 중 오류 발생", e);
                }
            }
        } catch (IOException e) {
            log.error("서버 실행 실패 [port={}]", SERVER_PORT, e);
        } finally {
            timeoutService.stop();
            workerThreadPool.stop();
            log.info("서버 종료 [port={}]", SERVER_PORT);
        }
    }
}
