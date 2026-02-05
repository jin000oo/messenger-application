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

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.net.Socket;
import java.util.concurrent.atomic.AtomicLong;

@AllArgsConstructor
@Getter
public class Session {
    private final String sessionId;
    private final String userId;
    @Setter
    private volatile Socket socket;
    private final AtomicLong lastSeenAt;

    public Session(String sessionId, String userId, Socket socket, long now) {
        this.sessionId = sessionId;
        this.userId = userId;
        this.socket = socket;
        this.lastSeenAt = new AtomicLong();
        this.lastSeenAt.set(now);
    }

    public long getLastSeenAt() {
        return lastSeenAt.get();
    }

    public void setLastSeenAt(long now) {
        lastSeenAt.set(now);
    }
}
