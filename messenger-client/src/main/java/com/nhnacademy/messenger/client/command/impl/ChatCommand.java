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
import com.nhnacademy.messenger.client.dto.ChatParams;
import com.nhnacademy.messenger.client.ui.ClientUI;
import com.nhnacademy.messenger.common.domain.MessageRequest;
import com.nhnacademy.messenger.common.domain.MessageType;
import com.nhnacademy.messenger.common.dto.request.ChatRequest;
import com.nhnacademy.messenger.common.util.MessageUtils;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;

@Command(method = "/chat")
public class ChatCommand implements ClientCommand<ChatParams> {

    @Override
    public ChatParams parse(String[] args) {
        if (args.length < 2) {
            throw new IllegalArgumentException("[ChatCommand] 보낼 메시지를 입력해주세요.");
        }

        String[] messageParts = Arrays.copyOfRange(args, 1, args.length);
        String message = String.join(" ", messageParts);

        if (message.trim().isEmpty()) {
            throw new IllegalArgumentException("[ChatCommand] 메시지 내용이 비어있습니다.");
        }

        return new ChatParams(message);
    }

    @Override
    public void execute(ChatParams params, ClientContext context) {
        ClientUI clientUI = context.getClientUI();

        if (!context.getClientSession().isAuthenticated()) {
            clientUI.displayMessage("해당 서비스를 이용하려면 로그인이 필요합니다.");
            return;
        }

        String sessionId = context.getClientSession().getSessionId();
        Long currentRoomId = context.getClientSession().getCurrentRoomId();

        if (currentRoomId == null) {
            clientUI.displayMessage("해당 서비스를 이용하려면 채팅방에 먼저 입장을 해야 합니다.");
            return;
        }

        MessageRequest<ChatRequest> request = new MessageRequest<>(
                new MessageRequest.RequestHeader(
                        MessageType.CHAT_MESSAGE,
                        LocalDateTime.now().toString(),
                        sessionId),
                new ChatRequest(currentRoomId, params.message())
        );

        try {
            MessageUtils.send(context.getSocket().getOutputStream(), request);

        } catch (IOException e) {
            clientUI.displayMessage(String.format("예상치 못한 오류: %s", e.getMessage()));
        }
    }

}
