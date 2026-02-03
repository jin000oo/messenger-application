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

import com.nhnacademy.messenger.client.observer.Observer;
import com.nhnacademy.messenger.client.session.ClientSession;
import com.nhnacademy.messenger.client.subject.EventType;
import com.nhnacademy.messenger.client.ui.ClientUI;
import com.nhnacademy.messenger.common.domain.MessageResponse;
import com.nhnacademy.messenger.common.domain.MessageType;
import java.util.Map;

public class ClientSessionObserver implements Observer {

    private final ClientUI clientUI;

    public ClientSessionObserver(ClientUI clientUI) {
        this.clientUI = clientUI;
    }

    @Override
    public EventType getEventType() {
        return EventType.RECV;
    }

    @Override
    public void updateMessage(MessageResponse response) {
        if (response.getHeader() == null) {
            return;
        }

        MessageType type = response.getHeader().getType();
        Map<String, Object> data = response.getData();

        // 로그인 성공 시 세션 ID 저장
        if (type == MessageType.LOGIN_SUCCESS) {
            if (data != null && data.containsKey("sessionId")) {
                ClientSession.setSessionId((String) data.get("sessionId"));
                ClientSession.setUserId((String) data.get("userId"));
            }
        }

        // 로그아웃 성공 시 세션 ID 지우기
        else if (type == MessageType.LOGOUT_SUCCESS) {
            ClientSession.setSessionId(null);
            ClientSession.setUserId(null);
            ClientSession.setCurrentRoomId(null);

            clientUI.displayMessage("로그아웃 성공");
        }

        // 채팅방 입장 성공 시 채팅방 ID 저장
        else if (type == MessageType.CHAT_ROOM_ENTER_SUCCESS) {
            ClientSession.setCurrentRoomId(((Number) data.get("roomId")).longValue());
        }

        // 채팅방 나가면 방 번호 초기화
        else if (type == MessageType.CHAT_ROOM_EXIT_SUCCESS) {
            ClientSession.setCurrentRoomId(null);
        }
    }

}
