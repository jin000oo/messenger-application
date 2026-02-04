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

package com.nhnacademy.messenger.server.session.impl;

import com.nhnacademy.messenger.server.session.Session;
import com.nhnacademy.messenger.server.session.SessionRepository;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MemorySessionRepository implements SessionRepository {

    // Key-SessionId
    private final Map<String, Session> sessions = new ConcurrentHashMap<>();
    // Key-UserId
    private final Map<String, Session> sessionsByUserId = new ConcurrentHashMap<>();

    private final Object lock = new Object();

    @Override
    public void add(Session session) {
        synchronized (lock) {
            Session previousSession = sessionsByUserId.put(session.getUserId(), session);
            // 이전 세션이 있다면
            // -> sessionsByUserId는 새로운 세션으로 덮어쓴다.
            // -> sessions(BySessionId)는 기존의 세션을 삭제하고, 새로운 세션을 추가한다.
            if (previousSession != null) {
                sessions.remove(previousSession.getSessionId());
                closeSocket(previousSession.getSocket());
            }

            sessions.put(session.getSessionId(), session);
        }
    }

    @Override
    public void remove(String sessionId) {
        synchronized (lock) {
            Session session = sessions.remove(sessionId);
            // 세션이 존재한다면
            if (session != null) {
                sessionsByUserId.remove(session.getUserId());
                closeSocket(session.getSocket());
            }
        }
    }

    @Override
    public void removeByUserId(String userId) {
        synchronized (lock) {
            Session session = sessionsByUserId.remove(userId);
            if (session != null) {
                sessions.remove(session.getSessionId());
                closeSocket(session.getSocket());
            }
        }
    }

    @Override
    public Session getSession(String sessionId) {
        return sessions.get(sessionId);
    }

    @Override
    public Session getByUserId(String userId) {
        return sessionsByUserId.get(userId);
    }

    @Override
    public void updateSocket(String sessionId, Socket newSocket) {
        synchronized (lock) {
            Session session = sessions.get(sessionId);
            if (session == null) return;

            // oldSocket == newSocket
            Socket socket = session.getSocket();
            if (socket == newSocket) return;

            // socket == null or socket != newSocket
            session.setSocket(newSocket);

            // 기존 소켓 Close.
            closeSocket(socket);
        }
    }

    @Override
    public void updateLastSeenAt(String sessionId, long now) {
        Session session = sessions.get(sessionId);
        if (session != null) {
            session.setLastSeenAt(now);
        }
    }

    @Override
    public List<Session> snapshot() {
        synchronized (lock) {
            // Session List
            return new ArrayList<>(sessions.values());
        }
    }

    private void closeSocket(Socket socket) {
        if (socket == null) return;

        try {
            socket.close();
        } catch (IOException e) {
        }
    }
}
