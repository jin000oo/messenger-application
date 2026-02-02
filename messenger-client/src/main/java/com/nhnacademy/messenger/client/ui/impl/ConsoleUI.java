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
import java.util.List;
import java.util.Map;

public class ConsoleUI implements ClientUI {

    @Override
    public void displayMessage(String message) {
        System.out.printf("[Client] %s\n", message);
    }

    @Override
    public void displayError(String message) {
        System.out.printf("[Error] %s\n", message);
    }

    @Override
    public void handleResponse(MessageResponse response) {
        MessageType type = response.getHeader().getType();
        Map<String, Object> data = response.getData();

        // 에러 처리
        if (type == MessageType.ERROR) {
            displayError(String.format("%s (코드: %s)", data.get("message"), data.get("code")));
            return;
        }

        // 채팅 메시지
        if (type == MessageType.CHAT_MESSAGE) {
            String sender = (String) data.get("senderId");
            String message = (String) data.get("message");

            System.out.printf("[%s] %s\n", sender, message);
        }

        // 귓속말
        else if (type == MessageType.PRIVATE_MESSAGE || type == MessageType.PRIVATE_MESSAGE_SUCCESS) {
            String sender = (String) data.getOrDefault("senderId", "System");
            String receiver = (String) data.getOrDefault("receiverId", "System");
            String message = (String) data.get("message");

            if (type == MessageType.PRIVATE_MESSAGE_SUCCESS) {
                System.out.printf("[Me > %s] %s\n", receiver, message);
            } else {
                System.out.printf("[%s > Me] %s\n", sender, message);
            }
        }

        // 목록 조회 (방/유저) 및 기타 시스템 메시지
        else {
            if (data != null && data.containsKey("message")) {
                System.out.printf("[System] %s\n", data.get("message"));
            }

            printLists(type, data);
        }

        System.out.print("> ");
    }

    private void printLists(MessageType type, Map<String, Object> data) {
        if (type == MessageType.CHAT_ROOM_LIST_SUCCESS) {
            List<Map<String, Object>> rooms = (List<Map<String, Object>>) data.get("rooms");

            if (rooms != null) {
                rooms.forEach(room -> System.out.printf("[%s] %s (%s명)\n",
                        room.get("roomId"), room.get("roomName"), room.get("userCount")));
            }
        } else if (type == MessageType.USER_LIST_SUCCESS) {
            List<Map<String, Object>> users = (List<Map<String, Object>>) data.get("users");

            if (users != null) {
                users.forEach(user -> System.out.printf("- %s (%s)\n",
                        user.get("id"), user.get("name")));
            }
        }
    }

}
