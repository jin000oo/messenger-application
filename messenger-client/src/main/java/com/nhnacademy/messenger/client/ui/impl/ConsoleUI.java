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
import com.nhnacademy.messenger.common.domain.MessageResponse;
import com.nhnacademy.messenger.common.domain.MessageType;
import com.nhnacademy.messenger.common.dto.message.ChatMessage;
import com.nhnacademy.messenger.common.dto.response.*;
import com.nhnacademy.messenger.common.dto.response.info.MessageInfo;
import com.nhnacademy.messenger.common.dto.response.info.RoomInfo;
import com.nhnacademy.messenger.common.dto.response.info.UserInfo;
import com.nhnacademy.messenger.common.util.MessageUtils;

public class ConsoleUI implements ClientUI {

    private final MessageUtils messageUtils;

    public ConsoleUI(MessageUtils messageUtils) {
        this.messageUtils = messageUtils;
    }

    @Override
    public void displayMessage(String message) {
        System.out.printf("[Client] %s\n", message);
        System.out.print("> ");
    }

    @Override
    public void displayError(String message) {
        System.out.printf("[Error] %s\n", message);
        System.out.print("> ");
    }

    @Override
    public void handleResponse(MessageResponse response) {
        MessageType type = response.getHeader().getType();

        // 에러 처리
        if (type.equals(MessageType.ERROR)) {
            ErrorResponse data = messageUtils.convertData(response.getData(), ErrorResponse.class);

            displayError(String.format("%s (Code: %s)", data.message(), data.code()));
            return;
        }

        // 채팅 메시지
        if (type.equals(MessageType.CHAT_MESSAGE)) {
            ChatMessage message = messageUtils.convertData(response.getData(), ChatMessage.class);

            System.out.printf("[%s] %s\n", message.senderId(), message.message());
        }

        // 귓속말
        else if (type.equals(MessageType.PRIVATE_MESSAGE) || type.equals(MessageType.PRIVATE_MESSAGE_SUCCESS)) {
            WhisperResponse data = messageUtils.convertData(response.getData(), WhisperResponse.class);

            if (type.equals(MessageType.PRIVATE_MESSAGE_SUCCESS)) {
                System.out.printf("[Me > %s] %s\n", data.receiverId(), data.message());
            } else {
                System.out.printf("[%s > Me] %s\n", data.senderId(), data.message());
            }
        }

        // 방 목록
        else if (type.equals(MessageType.CHAT_ROOM_LIST_SUCCESS)) {
            RoomListResponse rooms = messageUtils.convertData(response.getData(), RoomListResponse.class);

            if (rooms != null && !rooms.rooms().isEmpty()) {
                for (RoomInfo room : rooms.rooms()) {
                    System.out.printf("[%s] %s (%s명)\n", room.roomId(), room.roomName(), room.userCount());
                }
            } else {
                System.out.println("[System] 현재 생성된 채팅방이 없습니다.");
            }
        }

        // 유저 목록
        else if (type.equals(MessageType.USER_LIST_SUCCESS)) {
            UserListResponse users = messageUtils.convertData(response.getData(), UserListResponse.class);

            if (users != null && !users.users().isEmpty()) {
                for (UserInfo user : users.users()) {
                    System.out.printf("- %s (%s)\n", user.id(), user.name());
                }
            } else {
                System.out.println("[System] 현재 접속 중인 유저가 없습니다.");
            }
        }

        // 과거 채팅 기록
        else if (type.equals(MessageType.CHAT_MESSAGE_HISTORY_SUCCESS)) {
            HistoryResponse messages = messageUtils.convertData(response.getData(), HistoryResponse.class);

            if (messages != null && !messages.messages().isEmpty()) {
                for (MessageInfo message : messages.messages()) {
                    System.out.printf("[%s] %s: %s\n", message.timestamp(), message.senderId(), message.content());

                }
            } else {
                System.out.println("[System] 과거 채팅 기록이 없습니다.");
            }
        }

        System.out.print("> ");
    }

}
