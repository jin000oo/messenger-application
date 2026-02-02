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

package com.nhnacademy.messenger.server.message.repository.impl;

import com.nhnacademy.messenger.server.message.domain.ChatMessage;
import com.nhnacademy.messenger.server.message.repository.MessageRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MemoryMessageRepository implements MessageRepository {

    // roomId, ChatMessage List
    private final Map<Long, List<ChatMessage>> messages = new ConcurrentHashMap<>();

    @Override
    public void save(ChatMessage chatMessage) {
        messages.computeIfAbsent(chatMessage.getRoomId(), roomId -> new ArrayList<>()).add(chatMessage);
    }

    @Override
    public List<ChatMessage> findAll(long roomId, int limit) {
        if (!messages.containsKey(roomId)) {
            return Collections.emptyList();
        }

        return List.copyOf(messages.get(roomId).subList(0, Math.min(limit, messages.get(roomId).size())));
    }
}
