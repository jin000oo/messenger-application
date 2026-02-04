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
import com.nhnacademy.messenger.client.dto.HistoryParams;
import com.nhnacademy.messenger.client.ui.ClientUI;
import com.nhnacademy.messenger.common.domain.MessageRequest;
import com.nhnacademy.messenger.common.domain.MessageType;
import com.nhnacademy.messenger.common.dto.request.HistoryRequest;
import com.nhnacademy.messenger.common.util.MessageUtils;
import java.io.IOException;
import java.time.LocalDateTime;

@Command(method = "/history")
public class HistoryCommand implements ClientCommand<HistoryParams> {

    private static final int DEFAULT_LIMIT = 50;

    @Override
    public HistoryParams parse(String[] args) {
        // 기본 값: 가장 큰 값 (최신순)
        long beforeMessageId = Long.MAX_VALUE;

        if (args.length >= 2) {
            beforeMessageId = Long.parseLong(args[1]);
        }

        return new HistoryParams(beforeMessageId);
    }

    @Override
    public void execute(HistoryParams params, ClientContext context) {
        ClientUI clientUI = context.getClientUI();

        String sessionId = context.getClientSession().getSessionId();
        Long currentRoomId = context.getClientSession().getCurrentRoomId();

        if (currentRoomId == null) {
            clientUI.displayMessage("해당 서비스를 이용하려면 채팅방에 먼저 입장을 해야 합니다.");
            return;
        }

        MessageRequest<HistoryRequest> request = new MessageRequest<>(
                new MessageRequest.RequestHeader(
                        MessageType.CHAT_MESSAGE_HISTORY,
                        LocalDateTime.now().toString(),
                        sessionId),
                new HistoryRequest(currentRoomId, DEFAULT_LIMIT, params.beforeMessageId())
        );

        try {
            MessageUtils.send(context.getSocket().getOutputStream(), request);

        } catch (IOException e) {
            clientUI.displayMessage(String.format("예상치 못한 오류: %s", e.getMessage()));
        }
    }

}
