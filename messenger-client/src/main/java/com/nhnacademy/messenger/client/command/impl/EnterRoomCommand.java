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
import com.nhnacademy.messenger.client.dto.EnterRoomParams;
import com.nhnacademy.messenger.client.ui.ClientUI;
import com.nhnacademy.messenger.common.domain.MessageRequest;
import com.nhnacademy.messenger.common.domain.MessageType;
import com.nhnacademy.messenger.common.dto.request.EnterChatRoomRequest;
import com.nhnacademy.messenger.common.util.MessageUtils;
import java.io.IOException;
import java.time.LocalDateTime;

@Command(method = "/enter")
public class EnterRoomCommand implements ClientCommand<EnterRoomParams> {

    @Override
    public EnterRoomParams parse(String[] args) {
        if (args.length < 2) {
            throw new IllegalArgumentException("[EnterRoomCommand] 참여할 채팅방의 아이디를 입력해주세요.");
        }

        long roomId = Long.parseLong(args[1]);

        return new EnterRoomParams(roomId);
    }

    @Override
    public void execute(EnterRoomParams params, ClientContext context) {
        ClientUI clientUI = context.getClientUI();

        String sessionId = context.getClientSession().getSessionId();

        MessageRequest<EnterChatRoomRequest> request = new MessageRequest<>(
                new MessageRequest.RequestHeader(
                        MessageType.CHAT_ROOM_ENTER,
                        LocalDateTime.now().toString(),
                        sessionId),
                new EnterChatRoomRequest(params.roomId())
        );

        try {
            MessageUtils.send(context.getSocket().getOutputStream(), request);

        } catch (IOException e) {
            clientUI.displayMessage(String.format("예상치 못한 오류: %s", e.getMessage()));
        }
    }

}
