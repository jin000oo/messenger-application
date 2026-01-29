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
import com.nhnacademy.messenger.common.util.MessageUtils;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

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
            while ((request = MessageUtils.readRequest(in)) != null) {
                String json = objectMapper.writeValueAsString(request);
                log.debug("Request: {}", json);
f
                MessageUtils.send(out, request);
            }

        } catch (IOException e) {
            log.debug(e.getMessage());

        } finally {
            try {
                client.close();

            } catch (IOException e) {
                log.debug(e.getMessage());
            }
        }
    }
}
