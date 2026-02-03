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
import com.nhnacademy.messenger.common.dto.response.info.RoomInfo;
import com.nhnacademy.messenger.common.dto.response.info.UserInfo;
import com.nhnacademy.messenger.common.util.MessageUtils;
import java.util.function.Consumer;

public class SwingUI implements ClientUI {

    private final MessageClientForm form;

    public SwingUI(Consumer<String> onInputReceived) {
        this.form = new MessageClientForm(onInputReceived);
        this.form.setVisible(true);
    }

    @Override
    public void displayMessage(String message) {
        form.appendMessage(String.format("[Client] %s", message));
    }

    @Override
    public void displayError(String message) {
        form.appendMessage(String.format("[Error] %s", message));
    }

    @Override
    public void handleResponse(MessageResponse response) {
        MessageType type = response.getHeader().getType();

        // 에러 처리
        if (type.equals(MessageType.ERROR)) {
            ErrorResponse data = MessageUtils.convertData(response.getData(), ErrorResponse.class);

            displayError(String.format("%s (Code: %s)", data.message(), data.code()));
            return;
        }

        // 채팅 메시지
        if (type.equals(MessageType.CHAT_MESSAGE)) {
            ChatMessage message = MessageUtils.convertData(response.getData(), ChatMessage.class);

            form.appendMessage(String.format("[%s] %s", message.senderId(), message.message()));
        }

        // 귓속말
        else if (type.equals(MessageType.PRIVATE_MESSAGE) || type.equals(MessageType.PRIVATE_MESSAGE_SUCCESS)) {
            WhisperResponse data = MessageUtils.convertData(response.getData(), WhisperResponse.class);

            if (type.equals(MessageType.PRIVATE_MESSAGE_SUCCESS)) {
                form.appendMessage(String.format("[Me > %s] %s", data.receiverId(), data.message()));
            } else {
                form.appendMessage(String.format("[%s > Me] %s", data.senderId(), data.message()));
            }
        }

        // 방 목록
        else if (type.equals(MessageType.CHAT_ROOM_LIST_SUCCESS)) {
            RoomListResponse rooms = MessageUtils.convertData(response.getData(), RoomListResponse.class);

            if (rooms != null && !rooms.rooms().isEmpty()) {
                for (RoomInfo room : rooms.rooms()) {
                    form.appendMessage(String.format("[%s] %s (%s명)\n",
                            room.roomId(), room.roomName(), room.userCount()));
                }
            } else {
                form.appendMessage("[System] 현재 생성된 채팅방이 없습니다.");
            }
        }

        // 유저 목록
        else if (type.equals(MessageType.USER_LIST_SUCCESS)) {
            UserListResponse users = MessageUtils.convertData(response.getData(), UserListResponse.class);

            if (users != null && users.users().isEmpty()) {
                for (UserInfo user : users.users()) {
                    form.appendMessage(String.format("- %s (%s)\n", user.id(), user.name()));
                }
            } else {
                form.appendMessage("[System] 현재 접속 중인 유저가 없습니다.");
            }
        }

        // 과거 채팅 기록
        else if (type.equals(MessageType.CHAT_MESSAGE_HISTORY_SUCCESS)) {
            HistoryResponse messages = MessageUtils.convertData(response.getData(), HistoryResponse.class);

            if (messages != null && !messages.messages().isEmpty()) {
                for (MessageInfo message : messages.messages()) {
                    form.appendMessage(String.format("[%s] %s: %s\n",
                            message.timestamp(), message.senderId(), message.content()));

                }
            } else {
                form.appendMessage("[System] 과거 채팅 기록이 없습니다.");
            }
        }
    }

}
