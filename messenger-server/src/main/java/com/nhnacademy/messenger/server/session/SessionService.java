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

package com.nhnacademy.messenger.server.session;

import com.nhnacademy.messenger.server.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import java.net.Socket;
import java.util.UUID;

@RequiredArgsConstructor
public class SessionService {

    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;

    public boolean validateSession(String sessionId) {
        return sessionRepository.getSession(sessionId) != null;
    }

    public Session registerSession(String userId, Socket socket) {
        long now = System.currentTimeMillis();
        String sessionId = UUID.randomUUID().toString();
        Session session = new Session(sessionId, userId, socket, now);

        userRepository.find(userId).ifPresent(user -> user.setOnline(true));
        sessionRepository.add(session);

        return session;
    }

    public void removeSession(String sessionId) {
        Session session = sessionRepository.getSession(sessionId);
        if (session == null) return;

        userRepository.find(session.getUserId()).ifPresent(user -> user.setOnline(false));
        sessionRepository.remove(sessionId);
    }

    public void removeByUserId(String userId) {
        Session session = sessionRepository.getByUserId(userId);
        if (session == null) return;

        userRepository.find(userId).ifPresent(user -> user.setOnline(false));
        sessionRepository.removeByUserId(userId);
    }

    public void removeBySocket(Socket socket) {
        if (socket == null) return;

        Session target = sessionRepository.snapshot().stream()
                .filter(session -> session.getSocket() == socket)
                .findFirst()
                .orElse(null);
        if (target == null) return;

        userRepository.find(target.getUserId()).ifPresent(user -> user.setOnline(false));
        sessionRepository.removeByUserId(target.getUserId());
    }

    public void reconnect(String sessionId, Socket socket) {
        sessionRepository.updateSocket(sessionId, socket);
        sessionRepository.updateLastSeenAt(sessionId, System.currentTimeMillis());
    }

    public void updateLastSeenAt(String sessionId) {
        sessionRepository.updateLastSeenAt(sessionId, System.currentTimeMillis());
    }
}
