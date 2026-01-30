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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {

    private static final Map<String, Session> sessionsById = new ConcurrentHashMap<>();
    private static final Map<String, Session> sessionsByUserId = new ConcurrentHashMap<>();

    public static void addSession(Session session) {
        sessionsById.put(session.getSessionId(), session);
        sessionsByUserId.put(session.getUserId(), session);
    }

    public static void removeBySessionId(String sessionId) {
        Session session = sessionsById.remove(sessionId);
        if (session != null) {
            sessionsByUserId.remove(session.getUserId());
        }
    }

    public static void removeByUserId(String userId) {
        Session session = sessionsByUserId.remove(userId);
        if (session != null) {
            sessionsById.remove(session.getSessionId());
        }
    }

    public static Session findBySessionId(String sessionId) {
        return sessionsById.get(sessionId);
    }

    public static Session findByUserId(String userId) {
        return sessionsByUserId.get(userId);
    }
}
