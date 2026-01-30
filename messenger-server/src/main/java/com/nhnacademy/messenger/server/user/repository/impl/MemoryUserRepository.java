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

package com.nhnacademy.messenger.server.user.repository.impl;

import com.nhnacademy.messenger.server.user.domain.User;
import com.nhnacademy.messenger.server.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class MemoryUserRepository implements UserRepository {

    private static final Map<String, User> users = new ConcurrentHashMap<>();

    static {
        users.put("marco", new User("marco", "nhnacademy123", "marco"));
        resetOnline();
    }

    @Override
    public User save(User user) {
        users.put(user.getUserId(), user);
        return user;
    }

    @Override
    public void delete(User user) {
        users.remove(user.getUserId());
    }

    @Override
    public boolean exists(String userId) {
        return users.containsKey(userId);
    }

    @Override
    public Optional<User> find(String userId) {
        return Optional.ofNullable(users.get(userId));
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public void setOnline(String userId, boolean online) {
        User user = users.get(userId);
        if (user != null) {
            user.setOnline(online);
        }
    }

    private static void resetOnline() {
        users.values().forEach(user -> user.setOnline(false));
    }
}
