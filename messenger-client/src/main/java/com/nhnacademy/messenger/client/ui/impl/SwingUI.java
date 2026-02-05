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

package com.nhnacademy.messenger.client.ui.impl;

import com.nhnacademy.messenger.client.ui.ClientUI;
import com.nhnacademy.messenger.client.ui.swing.MessageClientForm;
import com.nhnacademy.messenger.common.domain.MessageResponse;
import com.nhnacademy.messenger.common.domain.MessageType;
import com.nhnacademy.messenger.common.dto.ChatMessage;
import com.nhnacademy.messenger.common.dto.response.ErrorResponse;
import com.nhnacademy.messenger.common.dto.response.HistoryResponse;
import com.nhnacademy.messenger.common.dto.response.RoomListResponse;
import com.nhnacademy.messenger.common.dto.response.UserListResponse;
import com.nhnacademy.messenger.common.dto.response.WhisperResponse;
import com.nhnacademy.messenger.common.dto.response.info.MessageInfo;
import com.nhnacademy.messenger.common.util.MessageUtils;
import java.util.function.Consumer;

public class SwingUI implements ClientUI {

    private final MessageUtils messageUtils;
    private final MessageClientForm form;

    public SwingUI(Consumer<String> onInputReceived, MessageUtils messageUtils) {
        this.messageUtils = messageUtils;
        this.form = new MessageClientForm(onInputReceived);
        this.form.setVisible(true);
    }

    @Override
    public void displayMessage(String message) {
        form.appendMessage("[System] " + message);
    }

    @Override
    public void displayError(String message) {
        // 단순 로그 출력이 아니라 팝업으로 띄워줌
        form.showErrorMessage(message);
    }

    @Override
    public void handleResponse(MessageResponse response) {
        MessageType type = response.getHeader().getType();

        // 로그인 성공 시 화면 전환
        if (type.equals(MessageType.LOGIN_SUCCESS)) {
            form.showScreen("CHAT");
            form.appendMessage("로그인 성공! [새로고침]을 눌러 방 목록을 확인하세요.");
        }

        // 로그아웃 성공 시 -> 로그인 화면으로
        else if (type.equals(MessageType.LOGOUT_SUCCESS)) {
            form.showScreen("LOGIN");
            displayError("로그아웃 되었습니다."); // 팝업으로 알려줌
        }

        // 채팅방 목록 응답이 오면 -> JList 업데이트
        else if (type.equals(MessageType.CHAT_ROOM_LIST_SUCCESS)) {
            RoomListResponse rooms = messageUtils.convertData(response.getData(), RoomListResponse.class);
            if (rooms != null) {
                form.updateRoomList(rooms.rooms());
            }
        }

        // 유저 목록 수신 시 -> 팝업창 띄우기
        else if (type.equals(MessageType.USER_LIST_SUCCESS)) {
            UserListResponse users = messageUtils.convertData(response.getData(), UserListResponse.class);
            if (users != null) {
                form.showUserListPopup(users.users());
            }
        }

        // 에러 메시지
        else if (type.equals(MessageType.ERROR)) {
            ErrorResponse data = messageUtils.convertData(response.getData(), ErrorResponse.class);
            displayError(String.format("%s (Code: %s)", data.message(), data.code()));
        }

        // 일반 채팅
        else if (type.equals(MessageType.CHAT_MESSAGE)) {
            ChatMessage message = messageUtils.convertData(response.getData(), ChatMessage.class);
            form.appendMessage(String.format("[%s] %s", message.senderId(), message.message()));
        }

        // 귓속말
        else if (type.equals(MessageType.PRIVATE_MESSAGE) || type.equals(MessageType.PRIVATE_MESSAGE_SUCCESS)) {
            WhisperResponse data = messageUtils.convertData(response.getData(), WhisperResponse.class);
            if (type.equals(MessageType.PRIVATE_MESSAGE_SUCCESS)) {
                form.appendMessage(String.format("[나 -> %s] %s", data.receiverId(), data.message()));
            } else {
                form.appendMessage(String.format("[%s -> 나] %s", data.senderId(), data.message()));
            }
        }

        // 과거 기록
        else if (type.equals(MessageType.CHAT_MESSAGE_HISTORY_SUCCESS)) {
            HistoryResponse messages = messageUtils.convertData(response.getData(), HistoryResponse.class);
            for (MessageInfo msg : messages.messages()) {
                form.appendMessage(String.format("[%s] %s: %s", msg.timestamp(), msg.senderId(), msg.content()));
            }
        }
    }

}
