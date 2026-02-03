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
import java.util.List;
import java.util.Map;
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
        Map<String, Object> data = response.getData();

        // 에러 처리
        if (type == MessageType.ERROR) {
            displayError(String.format("%s (Code: %s)", data.get("message"), data.get("code")));
            return;
        }

        // 채팅 메시지
        if (type == MessageType.CHAT_MESSAGE) {
            form.appendMessage(String.format("[%s] %s", data.get("senderId"), data.get("message")));
        }

        // 귓속말
        else if (type == MessageType.PRIVATE_MESSAGE || type == MessageType.PRIVATE_MESSAGE_SUCCESS) {
            String senderId = (String) data.getOrDefault("senderId", "System");
            String receiverId = (String) data.getOrDefault("receiverId", "System");
            String message = (String) data.get("message");

            if (type == MessageType.PRIVATE_MESSAGE_SUCCESS) {
                form.appendMessage(String.format("[Me > %s] %s", receiverId, message));
            } else {
                form.appendMessage(String.format("[%s > Me] %s", senderId, message));
            }
        }

        // 목록 조회 (방/유저) 및 기타 시스템 메시지
        else {
            if (data != null && data.containsKey("message")) {
                form.appendMessage(String.format("[System] %s", data.get("message")));
            }

            printLists(type, data);
        }
    }

    private void printLists(MessageType type, Map<String, Object> data) {
        // 방 목록
        if (type == MessageType.CHAT_ROOM_LIST_SUCCESS) {
            List<Map<String, Object>> rooms = (List<Map<String, Object>>) data.get("rooms");

            if (rooms != null) {
                rooms.forEach(room -> form.appendMessage(String.format("[%s] %s (%s명)",
                        room.get("roomId"), room.get("roomName"), room.get("userCount"))));
            }
        }

        // 유저 목록
        else if (type == MessageType.USER_LIST_SUCCESS) {
            List<Map<String, Object>> users = (List<Map<String, Object>>) data.get("users");

            if (users != null) {
                users.forEach(user -> form.appendMessage(String.format("- %s (%s)",
                        user.get("id"), user.get("name"))));
            }
        }

        // 과거 채팅 기록
        else if (type == MessageType.CHAT_MESSAGE_HISTORY_SUCCESS) {
            List<Map<String, Object>> messages = (List<Map<String, Object>>) data.get("messages");

            if (messages != null) {
                messages.forEach(message -> {
                    String senderId = (String) message.get("senderId");
                    String content = (String) message.get("content");
                    String timestamp = (String) message.get("timestamp");

                    form.appendMessage(String.format("[%s] %s: %s", timestamp, senderId, content));
                });
            }
        }
    }

}
