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

import com.nhnacademy.messenger.server.message.domain.PrivateMessage;
import com.nhnacademy.messenger.server.message.repository.PrivateMessageRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MemoryPrivateMessageRepository implements PrivateMessageRepository {

    private final Map<String, List<PrivateMessage>> privateMessages = new ConcurrentHashMap<>();

    @Override
    public void save(PrivateMessage privateMessage) {
        privateMessages.computeIfAbsent(privateMessage.getSenderId(), senderId -> new ArrayList<>()).add(privateMessage);
    }

    @Override
    public List<PrivateMessage> findAll(String userId) {
        if (privateMessages.containsKey(userId)) {
            return Collections.emptyList();
        }

        return List.copyOf(privateMessages.get(userId));
    }
}
