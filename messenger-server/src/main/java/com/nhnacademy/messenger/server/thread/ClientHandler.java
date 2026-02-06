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
import com.nhnacademy.messenger.common.domain.MessageRequest;
import com.nhnacademy.messenger.common.util.MessageUtils;
import com.nhnacademy.messenger.server.handler.MessageDispatcher;
import com.nhnacademy.messenger.server.thread.channel.DispatchJob;
import com.nhnacademy.messenger.server.thread.channel.NotificationChannel;
import com.nhnacademy.messenger.server.thread.channel.RequestChannel;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.SocketException;

@Slf4j
@AllArgsConstructor
public class ClientHandler implements Runnable {

    private final Socket socket;
    private final RequestChannel channel;
    private final NotificationChannel notificationChannel;
    private final MessageDispatcher dispatcher;
    private final MessageSender sender;
    private final MessageUtils messageUtils;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void run() {
        String ip = socket.getInetAddress().getHostAddress();
        int port = socket.getPort();

        try (socket;
             InputStream in = socket.getInputStream()
        ) {
            while (!Thread.currentThread().isInterrupted() && !socket.isClosed()) {
                MessageRequest<?> request = messageUtils.readRequest(in);
                // 클라이언트 정상 종료
                if (request == null) break;
                log.debug("[{}:{}] 요청: {}", ip, port, objectMapper.writeValueAsString(request));

                channel.put(new DispatchJob(socket, request, dispatcher, sender, notificationChannel));
            }

            log.info("[{}:{}] 클라이언트 연결 종료", ip, port);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (SocketException e) {
            log.debug("[{}:{}] 클라이언트 연결 끊김: {}", ip, port, e.getMessage());
        } catch (IOException e) {
            log.warn("[{}:{}] 클라이언트 통신 중 오류 발생", ip, port, e);
        } catch (RuntimeException e) {
            log.warn("[{}:{}] 클라이언트 통신 중 예기치 않은 오류 발생", ip, port, e);
        }
    }
}
