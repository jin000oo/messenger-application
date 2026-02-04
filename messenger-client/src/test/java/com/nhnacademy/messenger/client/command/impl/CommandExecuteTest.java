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

package com.nhnacademy.messenger.client.command.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.messenger.client.context.ClientContext;
import com.nhnacademy.messenger.client.dto.LoginParams;
import com.nhnacademy.messenger.client.session.ClientSession;
import com.nhnacademy.messenger.client.ui.ClientUI;
import com.nhnacademy.messenger.common.util.MessageUtils;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.Socket;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class CommandExecuteTest {

    @Test
    @DisplayName("LoginCommand 실행 시 소켓으로 데이터가 전송되어야 한다")
    void loginExecuteTest() throws IOException {
        Socket mockSocket = Mockito.mock(Socket.class);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Mockito.when(mockSocket.getOutputStream()).thenReturn(outputStream);

        ClientSession session = new ClientSession();
        ClientUI mockUI = Mockito.mock(ClientUI.class);
        MessageUtils messageUtils = new MessageUtils(new ObjectMapper());

        ClientContext context = new ClientContext(session, mockUI, mockSocket, messageUtils);

        LoginCommand command = new LoginCommand();
        LoginParams params = new LoginParams("marco", "1234");

        command.execute(params, context);

        String sentData = outputStream.toString();

        Assertions.assertTrue(sentData.contains("\"type\":\"LOGIN\""));
        Assertions.assertTrue(sentData.contains("\"userId\":\"marco\""));
    }

}
