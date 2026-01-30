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

package com.nhnacademy.messenger.client.observer.console;

import com.nhnacademy.messenger.client.observer.Observer;
import com.nhnacademy.messenger.client.subject.EventType;
import com.nhnacademy.messenger.common.domain.MessageResponse;
import com.nhnacademy.messenger.common.domain.MessageType;
import java.util.List;
import java.util.Map;

public class ConsoleRecvObserver implements Observer {

    @Override
    public EventType getEventType() {
        return EventType.RECV;
    }

    @Override
    public void updateMessage(MessageResponse response) {
        MessageType type = response.getHeader().getType();
        Map<String, Object> data = response.getData();

        // 에러 메시지
        if (type == MessageType.ERROR) {
            System.out.printf("[Error] %s (코드: %s)%s", data.get("message"), data.get("code"), System.lineSeparator());

        }

        // 모든 사용자 목록
        else if (type == MessageType.USER_LIST_SUCCESS) {
            List<Map<String, Object>> users = null;

            if (data.containsKey("users")) {
                users = (List<Map<String, Object>>) data.get("users");
            } else if (data.containsKey("data")) {
                users = (List<Map<String, Object>>) data.get("data");
            }

            if (users == null || users.isEmpty()) {
                System.out.println("현재 접속 중인 사용자가 없습니다.");
            } else {
                for (Map<String, Object> user : users) {
                    String id = (String) user.get("id");
                    String name = (String) user.get("name");
                    boolean online = (boolean) user.get("online");

                    String status = online ? "온라인" : "오프라인";

                    System.out.printf("[%s] %s (%s)%s", id, name, status, System.lineSeparator());
                }
            }
        }

        // 채팅 수신
        else if (type == MessageType.CHAT_MESSAGE_SUCCESS) {
            System.out.printf("[메시지]: %s%s", data.get("message"), System.lineSeparator());

        }

        // 채팅방 목록 조회
        else if (type == MessageType.CHAT_ROOM_LIST_SUCCESS) {
            List<Map<String, Object>> rooms = (List<Map<String, Object>>) data.get("rooms");

            if (rooms == null || rooms.isEmpty()) {
                System.out.println("현재 생성된 채팅방이 없습니다.");
            } else {
                for (Map<String, Object> room : rooms) {
                    System.out.printf("[%s] %s (인원: %s)%s",
                            room.get("roomId"), room.get("roomName"), room.get("userCount"), System.lineSeparator());
                }
            }
        }

        // 일반 시스템 메시지
        else {
            if (data != null && data.containsKey("message")) {
                System.out.printf("[시스템]: %s%s", data.get("message"), System.lineSeparator());
            }
        }

        System.out.print("> ");
    }

}
