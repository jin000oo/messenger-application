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
import com.nhnacademy.messenger.client.dto.WhisperParams;
import com.nhnacademy.messenger.client.ui.ClientUI;
import com.nhnacademy.messenger.common.domain.MessageRequest;
import com.nhnacademy.messenger.common.domain.MessageType;
import com.nhnacademy.messenger.common.dto.request.WhisperRequest;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;

@Command(method = "/whisper")
public class WhisperCommand implements ClientCommand<WhisperParams> {

    @Override
    public WhisperParams parse(String[] args) {
        if (args.length < 3) {
            throw new IllegalArgumentException("[WhisperCommand] 보낼 사람의 아이디와 메시지를 입력해주세요.");
        }

        String targetId = args[1];
        String[] messageParts = Arrays.copyOfRange(args, 2, args.length);
        String message = String.join(" ", messageParts);

        if (message.trim().isEmpty()) {
            throw new IllegalArgumentException("[WhisperCommand] 메시지 내용이 비어있습니다.");
        }

        return new WhisperParams(targetId, message);
    }

    @Override
    public void execute(WhisperParams params, ClientContext context) {
        ClientUI clientUI = context.getClientUI();

        String userId = context.getClientSession().getUserId();
        String sessionId = context.getClientSession().getSessionId();

        MessageRequest<WhisperRequest> request = new MessageRequest<>(
                new MessageRequest.RequestHeader(
                        MessageType.PRIVATE_MESSAGE,
                        LocalDateTime.now().toString(),
                        sessionId),
                new WhisperRequest(userId, params.targetId(), params.message())
        );

        try {
            context.getMessageUtils().send(context.getSocket().getOutputStream(), request);

        } catch (IOException e) {
            clientUI.displayMessage(String.format("예상치 못한 오류: %s", e.getMessage()));
        }
    }

}
