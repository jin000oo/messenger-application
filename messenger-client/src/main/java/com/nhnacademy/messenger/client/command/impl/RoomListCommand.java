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
import com.nhnacademy.messenger.client.session.ClientSession;
import com.nhnacademy.messenger.common.domain.MessageRequest;
import com.nhnacademy.messenger.common.domain.MessageType;
import com.nhnacademy.messenger.common.util.MessageUtils;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.util.Collections;

public class RoomListCommand implements ClientCommand {

    @Override
    public void execute(String[] args, OutputStream out) {
        String sessionId = ClientSession.getSessionId();

        if (sessionId == null) {
            System.out.println("[Client] 해당 서비스를 이용하려면 로그인이 필요합니다.");
            return;
        }

        MessageRequest request = new MessageRequest(
                new MessageRequest.RequestHeader(
                        MessageType.CHAT_ROOM_LIST,
                        LocalDateTime.now().toString(),
                        sessionId),
                Collections.emptyMap()
        );

        try {
            MessageUtils.send(out, request);

        } catch (IOException e) {
            System.out.printf("[Client] 예상치 못한 오류: %s\n", e.getMessage());
        }
    }

}
