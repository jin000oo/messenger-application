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

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class SessionManager {

    private static final Map<String, Session> sessionsById = new ConcurrentHashMap<>();
    private static final Map<String, Session> sessionsByUserId = new ConcurrentHashMap<>();

    private static final Object LOCK = new Object();

    // 로그인 상태에서 로그인 요청 시 기존 세션 삭제하고, 세션 업데이트
    // 로그아웃 하지 않고, 비정상 종료 후 다시 로그인 시 세션 업데이트
    public static void addSession(Session session) {
        synchronized (LOCK) {
            Session previousSession = sessionsByUserId.put(session.getUserId(), session);
            // sessionsById에서 이전 세션이 제거한다.
            if (previousSession != null) {
                sessionsById.remove(previousSession.getSessionId());

                // 이전 세션의 소켓 종료한다.
                if (previousSession.getSocket() != session.getSocket()) {
                    try {
                        previousSession.getSocket().close();
                    } catch (IOException e) {
                        log.debug("세션 추가 중 소켓 종료 실패 (userId={}): {}", session.getUserId(), e.getMessage());
                    }
                }
            }

            sessionsById.put(session.getSessionId(), session);
        }
    }

    public static void removeBySessionId(String sessionId) {
        synchronized (LOCK) {
            Session session = sessionsById.remove(sessionId);
            if (session != null) {
                sessionsByUserId.remove(session.getUserId());
            }
        }
    }

    public static void removeByUserId(String userId) {
        synchronized (LOCK) {
            Session session = sessionsByUserId.remove(userId);
            if (session != null) {
                sessionsById.remove(session.getSessionId());
            }
        }
    }

    public static Session findBySessionId(String sessionId) {
        return sessionsById.get(sessionId);
    }

    public static Session findByUserId(String userId) {
        return sessionsByUserId.get(userId);
    }

    // 재접속 시 소켓 업데이트
    // 소켓 변경 시 소켓 업데이트
    // 다시 로그인 하지 않고, 유효한 세션Id로 요청 시 소켓 업데이트
    public static void updateSocket(String sessionId, Socket newSocket) {
        synchronized (LOCK) {
            Session session = sessionsById.get(sessionId);
            if (session == null) return;

            Socket socket = session.getSocket();
            if (socket == newSocket) return;

            session.setSocket(newSocket);

            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    log.debug("소켓 업데이트 중 소켓 종료 실패 (userId={}): {}", session.getUserId(), e.getMessage());
                }
            }
        }
    }
}
