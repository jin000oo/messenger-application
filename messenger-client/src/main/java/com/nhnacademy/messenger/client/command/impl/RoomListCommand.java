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
import com.nhnacademy.messenger.client.ui.ClientUI;
import com.nhnacademy.messenger.common.domain.MessageRequest;
import com.nhnacademy.messenger.common.domain.MessageType;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;

@Command(method = "/list")
public class RoomListCommand implements ClientCommand<Void> {

    @Override
    public Void parse(String[] args) {
        return null;
    }

    @Override
    public void execute(Void params, ClientContext context) {
        ClientUI clientUI = context.getClientUI();

        String sessionId = context.getClientSession().getSessionId();

        MessageRequest<Object> request = new MessageRequest<>(
                new MessageRequest.RequestHeader(
                        MessageType.CHAT_ROOM_LIST,
                        LocalDateTime.now().toString(),
                        sessionId),
                Collections.emptyMap()
        );

        try {
            context.getMessageUtils().send(context.getSocket().getOutputStream(), request);

        } catch (IOException e) {
            clientUI.displayMessage(String.format("예상치 못한 오류: %s", e.getMessage()));
        }
    }

}
