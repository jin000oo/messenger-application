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

package com.nhnacademy.messenger.server.chatroom.chatroomrepository.impl;

import com.nhnacademy.messenger.server.chatroom.chatroomrepository.ChatRoomRepository;
import com.nhnacademy.messenger.server.chatroom.domain.ChatRoom;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class MemoryChatRoomRepository implements ChatRoomRepository {

    private final Map<Long, ChatRoom> rooms = new ConcurrentHashMap<>();

    @Override
    public ChatRoom save(ChatRoom chatRoom) {
        if (chatRoom != null) {
            rooms.put(chatRoom.getRoomId(), chatRoom);
        }

        return chatRoom;
    }

    @Override
    public void delete(long roomId) {
        rooms.remove(roomId);
    }

    @Override
    public Optional<ChatRoom> findById(long roomId) {
        return Optional.ofNullable(rooms.get(roomId));
    }

    @Override
    public List<ChatRoom> findAll() {
        return List.copyOf(rooms.values());
    }

    @Override
    public boolean exists(long roomId) {
        return rooms.containsKey(roomId);
    }

    @Override
    public boolean existsByRoomName(String roomName) {
        return rooms.values().stream().anyMatch(room -> room.getRoomName().equals(roomName));
    }
}
