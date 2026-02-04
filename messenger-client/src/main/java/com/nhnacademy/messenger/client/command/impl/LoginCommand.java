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

import com.nhnacademy.messenger.client.command.ClientCommand;
import com.nhnacademy.messenger.client.command.Command;
import com.nhnacademy.messenger.client.context.ClientContext;
import com.nhnacademy.messenger.client.dto.LoginParams;
import com.nhnacademy.messenger.client.ui.ClientUI;
import com.nhnacademy.messenger.common.domain.MessageRequest;
import com.nhnacademy.messenger.common.domain.MessageType;
import com.nhnacademy.messenger.common.dto.request.LoginRequest;
import com.nhnacademy.messenger.common.util.MessageUtils;
import java.io.IOException;
import java.time.LocalDateTime;

@Command(method = "/login")
public class LoginCommand implements ClientCommand<LoginParams> {

    @Override
    public LoginParams parse(String[] args) {
        if (args.length < 3) {
            throw new IllegalArgumentException("[LoginCommand] 아이디와 비밀번호를 입력해주세요.");
        }

        String userId = args[1];
        String password = args[2];

        return new LoginParams(userId, password);
    }

    @Override
    public void execute(LoginParams params, ClientContext context) {
        ClientUI clientUI = context.getClientUI();

        if (context.getClientSession().isAuthenticated()) {
            clientUI.displayMessage("이미 로그인되어 있습니다.");
            return;
        }

        MessageRequest<LoginRequest> request = new MessageRequest<>(
                new MessageRequest.RequestHeader(
                        MessageType.LOGIN,
                        LocalDateTime.now().toString(),
                        null),
                new LoginRequest(params.id(), params.password())
        );

        try {
            MessageUtils.send(context.getSocket().getOutputStream(), request);

        } catch (IOException e) {
            clientUI.displayMessage(String.format("예상치 못한 오류: %s", e.getMessage()));
        }
    }

}
