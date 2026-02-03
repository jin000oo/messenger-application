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

package com.nhnacademy.messenger.common.util;

import com.nhnacademy.messenger.common.domain.MessageRequest;
import com.nhnacademy.messenger.common.domain.MessageResponse;
import com.nhnacademy.messenger.common.domain.MessageType;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class MessageUtilsTest {

    @Test
    @DisplayName("Request 객체를 전송하고 다시 읽었을 때 값 유지")
    void sendAndReadRequestTest() throws IOException {
        MessageRequest.RequestHeader header =
                new MessageRequest.RequestHeader(MessageType.LOGIN, LocalDateTime.now().toString(), "test-session-id");
        Map<String, Object> body = Map.of("userId", "marco", "password", "1234");
        MessageRequest originalRequest = new MessageRequest(header, body);

        // 가짜 네트워크(ByteArrayStream)에 전송 (직렬화)
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        MessageUtils.send(output, originalRequest);

        // 전송된 데이터를 다시 읽기 (역직렬화)
        ByteArrayInputStream input = new ByteArrayInputStream(output.toByteArray());
        MessageRequest receivedRequest = MessageUtils.readRequest(input);

        // 보낸 것 == 받은 것
        Assertions.assertNotNull(receivedRequest);
        Assertions.assertEquals(MessageType.LOGIN, receivedRequest.getHeader().getType());
        Assertions.assertEquals("test-session-id", receivedRequest.getHeader().getSessionId());
        Assertions.assertEquals("marco", receivedRequest.getData().get("userId"));
    }

    @Test
    @DisplayName("Response 객체를 전송하고 다시 읽었을 때 값 유지")
    void sendAndReadResponseTest() throws IOException {
        MessageResponse.ResponseHeader header =
                new MessageResponse.ResponseHeader(MessageType.LOGIN_SUCCESS, LocalDateTime.now().toString(), true);
        Map<String, Object> body = Map.of("message", "로그인 성공");
        MessageResponse originalResponse = new MessageResponse(header, body);

        // 가짜 네트워크(ByteArrayStream)에 전송 (직렬화)
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        MessageUtils.send(output, originalResponse);

        // 전송된 데이터를 다시 읽기 (역직렬화)
        ByteArrayInputStream input = new ByteArrayInputStream(output.toByteArray());
        MessageResponse receivedResponse = MessageUtils.readResponse(input);

        // 보낸 것 == 받은 것
        Assertions.assertNotNull(receivedResponse);
        Assertions.assertTrue(receivedResponse.getHeader().isSuccess());
        Assertions.assertEquals(MessageType.LOGIN_SUCCESS, receivedResponse.getHeader().getType());
        Assertions.assertEquals("로그인 성공", receivedResponse.getData().get("message"));
    }

}
