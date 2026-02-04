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

package com.nhnacademy.messenger.client.observer.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.messenger.client.session.ClientSession;
import com.nhnacademy.messenger.client.ui.ClientUI;
import com.nhnacademy.messenger.common.domain.MessageResponse;
import com.nhnacademy.messenger.common.domain.MessageType;
import com.nhnacademy.messenger.common.dto.response.LoginResponse;
import com.nhnacademy.messenger.common.util.MessageUtils;
import java.time.LocalDateTime;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ClientSessionObserverTest {

    ClientSession clientSession;
    MessageUtils messageUtils;
    ClientSessionObserver observer;
    ClientUI mockUI = new ClientUI() {
        @Override
        public void displayMessage(String message) {
        }

        @Override
        public void displayError(String message) {
        }

        @Override
        public void handleResponse(MessageResponse response) {
        }
    };

    @BeforeEach
    void setUp() {
        clientSession = new ClientSession();
        messageUtils = new MessageUtils(new ObjectMapper());
        observer = new ClientSessionObserver(mockUI, clientSession, messageUtils);
    }

    @Test
    @DisplayName("LOGIN_SUCCESS 응답을 받으면 세션 정보가 저장되어야 한다")
    void loginSuccessUpdateTest() {
        LoginResponse loginData = new LoginResponse("marco", "test-uuid-1234", "Welcome");

        Map<String, Object> dataMap = new ObjectMapper().convertValue(loginData, Map.class);

        MessageResponse response = new MessageResponse(
                new MessageResponse.ResponseHeader(MessageType.LOGIN_SUCCESS, LocalDateTime.now().toString(), true),
                dataMap
        );

        observer.updateMessage(response);

        Assertions.assertTrue(clientSession.isAuthenticated());
        Assertions.assertEquals("test-uuid-1234", clientSession.getSessionId());
        Assertions.assertEquals("marco", clientSession.getUserId());
    }

    @Test
    @DisplayName("LOGOUT_SUCCESS 응답을 받으면 세션 정보가 초기화되어야 한다")
    void logoutSuccessUpdateTest() {
        clientSession.setSessionId("old-session");
        clientSession.setUserId("marco");

        MessageResponse response = new MessageResponse(
                new MessageResponse.ResponseHeader(MessageType.LOGOUT_SUCCESS, LocalDateTime.now().toString(), true),
                Map.of("message", "Logged out")
        );

        observer.updateMessage(response);

        Assertions.assertFalse(clientSession.isAuthenticated());
        Assertions.assertNull(clientSession.getSessionId());
    }

}
