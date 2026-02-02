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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.messenger.common.domain.MessageRequest;
import com.nhnacademy.messenger.common.domain.MessageResponse;
import com.nhnacademy.messenger.common.util.MessageUtils;
import com.nhnacademy.messenger.server.handler.MessageDispatcher;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;

@Slf4j
public class ClientHandler implements Runnable {

    private final Socket client;
    private final MessageDispatcher messageDispatcher;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public ClientHandler(Socket client, MessageDispatcher messageDispatcher) {
        this.client = client;
        this.messageDispatcher = messageDispatcher;
    }

    @Override
    public void run() {
        String ip = client.getInetAddress().getHostAddress();
        int port = client.getPort();

        try (client;
             InputStream in = client.getInputStream();
             OutputStream out = client.getOutputStream()
        ) {
            MessageRequest request;
            // null 클라이언트 정상 종료
            while ((request = MessageUtils.readRequest(in)) != null) {
                MessageResponse response = messageDispatcher.dispatch(request, client);
                log.debug("[{}:{}] 요청: {}", ip, port, objectMapper.writeValueAsString(request));

                MessageUtils.send(out, response);
                log.debug("[{}:{}] 응답: {}", ip, port, objectMapper.writeValueAsString(response));
            }

            log.info("[{}:{}] 클라이언트 연결 종료", ip, port);
            // SocketException 클라이언트 비정상 종료
        } catch (SocketException e) {
            log.debug("[{}:{}] 클라이언트 연결 끊김: {}", ip, port, e.getMessage());
        } catch (RuntimeException e) {
            log.warn("[{}:{}] 클라이언트 통신 중 예기치 않은 오류 발생", ip, port, e);
        } catch (IOException e) {
            log.warn("[{}:{}] 클라이언트 통신 중 오류 발생", ip, port, e);
        }
    }
}
