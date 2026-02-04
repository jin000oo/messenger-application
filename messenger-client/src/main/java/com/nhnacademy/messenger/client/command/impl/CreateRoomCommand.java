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
import com.nhnacademy.messenger.client.dto.CreateRoomParams;
import com.nhnacademy.messenger.client.ui.ClientUI;
import com.nhnacademy.messenger.common.domain.MessageRequest;
import com.nhnacademy.messenger.common.domain.MessageType;
import com.nhnacademy.messenger.common.dto.request.CreateChatRoomRequest;
import com.nhnacademy.messenger.common.util.MessageUtils;
import java.io.IOException;
import java.time.LocalDateTime;

@Command(method = "/create")
public class CreateRoomCommand implements ClientCommand<CreateRoomParams> {

    @Override
    public CreateRoomParams parse(String[] args) {
        if (args.length < 2) {
            throw new IllegalArgumentException("[CreateRoomCommand] 생성할 채팅방의 이름을 입력해주세요.");
        }

        String roomName = args[1];

        return new CreateRoomParams(roomName);
    }

    @Override
    public void execute(CreateRoomParams params, ClientContext context) {
        ClientUI clientUI = context.getClientUI();

        String sessionId = context.getClientSession().getSessionId();

        MessageRequest<CreateChatRoomRequest> request = new MessageRequest<>(
                new MessageRequest.RequestHeader(
                        MessageType.CHAT_ROOM_CREATE,
                        LocalDateTime.now().toString(),
                        sessionId),
                new CreateChatRoomRequest(params.roomName())
        );

        try {
            MessageUtils.send(context.getSocket().getOutputStream(), request);

        } catch (IOException e) {
            clientUI.displayMessage(String.format("예상치 못한 오류: %s", e.getMessage()));
        }
    }

}
