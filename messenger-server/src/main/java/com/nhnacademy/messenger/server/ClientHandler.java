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
import com.nhnacademy.messenger.common.domain.MessageType;
import com.nhnacademy.messenger.common.util.MessageUtils;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;

@Slf4j
@NoArgsConstructor
public class ClientHandler implements Runnable {

    private Socket client;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public ClientHandler(Socket client) {
        this.client = client;
    }

    @Override
    public void run() {
        try (InputStream in = client.getInputStream();
             OutputStream out = client.getOutputStream();
        ) {
            MessageRequest request;
            MessageResponse response;
            while ((request = MessageUtils.readRequest(in)) != null) {
                log.debug("request: {}", objectMapper.writeValueAsString(request));

                if (request.getHeader().getType() == MessageType.LOGIN) {
                    response = new MessageResponse(
                            new MessageResponse.ResponseHeader(
                                    MessageType.LOGIN_SUCCESS,
                                    LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).toString(),
                                    true),
                            Map.of(
                                    "userId", request.getData().get("userId"),
                                    "sessionID", "sessionId",
                                    "message", "welcome"
                            )
                    );

                    MessageUtils.send(out, response);
                }
            }

        } catch (IOException e) {
            log.debug(e.getMessage());

        } finally {
            try {
                log.debug("[-] {}:{}", client.getInetAddress().getHostName(), client.getPort());
                client.close();

            } catch (IOException e) {
                log.debug(e.getMessage());
            }
        }
    }
}
