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

package com.nhnacademy.messenger.server.chatroom.domain;

import lombok.Getter;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class ChatRoom {

    private final long roomId;
    private final String roomName;
    private final Set<String> members = ConcurrentHashMap.newKeySet();

    public ChatRoom(long roomId, String roomName) {
        this.roomId = roomId;
        this.roomName = roomName;
    }

    public boolean addMember(String userId) {
        if (userId == null) {
            return false;
        }

        return members.add(userId);
    }

    public boolean removeMember(String userId) {
        return members.remove(userId);
    }

    public boolean hasMember(String userId) {
        return members.contains(userId);
    }

    public Set<String> getAllMembers() {
        return Set.copyOf(members);
    }

    public int memberCount() {
        return members.size();
    }
}
