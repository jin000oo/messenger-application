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

package com.nhnacademy.messenger.server.notification;

import com.nhnacademy.messenger.common.domain.ContentType;
import com.nhnacademy.messenger.common.domain.MessageType;
import com.nhnacademy.messenger.common.dto.push.NewMessagePush;
import com.nhnacademy.messenger.common.dto.push.RoomEnterPush;
import com.nhnacademy.messenger.common.dto.push.RoomExitPush;
import com.nhnacademy.messenger.server.chatroom.chatroomrepository.ChatRoomRepository;
import com.nhnacademy.messenger.server.chatroom.domain.ChatRoom;
import com.nhnacademy.messenger.server.thread.MessageSender;
import com.nhnacademy.messenger.server.utils.ResponseFactory;
import lombok.RequiredArgsConstructor;

import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
public class NotificationService {

    private final ChatRoomRepository chatRoomRepository;
    private final MessageSender sender;

    public void pushNewMessage(long roomId,
                               long messageId,
                               String senderId,
                               String content,
                               ContentType type,
                               String fileName,
                               long fileSize) {

        List<String> members = getMembers(roomId, senderId);
        if (members.isEmpty()) return;

        if (type == ContentType.TEXT) {
            sender.sendToUsers(members, ResponseFactory.successResponse(
                    MessageType.PUSH_NEW_MESSAGE,
                    new NewMessagePush(roomId, messageId, senderId, content, type, null, 0L)
            ));
        } else if (type == ContentType.FILE) {
            sender.sendToUsers(members, ResponseFactory.successResponse(
                    MessageType.PUSH_NEW_MESSAGE,
                    new NewMessagePush(roomId, messageId, senderId, content, type, fileName, fileSize)
            ));
        }
    }

    public void pushRoomEnter(long roomId,
                              String userId,
                              String userName) {
        List<String> members = getMembers(roomId, userId);
        if (members.isEmpty()) return;

        sender.sendToUsers(members, ResponseFactory.successResponse(
                MessageType.PUSH_ROOM_ENTER,
                new RoomEnterPush(roomId, userId, userName)
        ));
    }

    public void pushRoomExit(long roomId,
                             String userId) {
        List<String> members = getMembers(roomId, userId);
        if (members.isEmpty()) return;

        sender.sendToUsers(members, ResponseFactory.successResponse(
                MessageType.PUSH_ROOM_EXIT,
                new RoomExitPush(roomId, userId)
        ));
    }

    public List<String> getMembers(long roomId, String senderId) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElse(null);
        if (chatRoom == null) return Collections.emptyList();

        return chatRoom.getAllMembers().stream()
                .filter(userId -> !userId.equals(senderId))
                .toList();
    }
}
