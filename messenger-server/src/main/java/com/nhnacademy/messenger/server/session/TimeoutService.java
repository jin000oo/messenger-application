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

import com.nhnacademy.messenger.server.thread.MessageSender;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
@AllArgsConstructor
public class TimeoutService {
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    private final SessionRepository sessionRepository;
    private final SessionService sessionService;
    private final MessageSender sender;

    private final long interval;
    private final long timeout;

    public void start() {
        scheduler.scheduleAtFixedRate(this::tick, interval, interval, TimeUnit.MILLISECONDS);
    }

    public void stop() {
        scheduler.shutdownNow();
    }

    private void tick() {
        long now = System.currentTimeMillis();

        for (Session session : sessionRepository.snapshot()) {
            long last = session.getLastSeenAt();

            if (now - last > timeout) {
                log.info("세션 타임아웃 [userId={}, sessionId={}]", session.getUserId(), session.getSessionId());

//                sender.sendToUser(session.getUserId(),
//                        ResponseFactory.success(MessageType.LOGOUT_SUCCESS, new LogoutResponse("세션 타임아웃")));
                sessionService.removeByUserId(session.getUserId());
                continue;
            }

//            sender.send(
//                    session.getSocket(),
//                    ResponseFactory.success(
//                            MessageType.PRIVATE_MESSAGE,
//                            Map.of("timestamp", now)));
        }
    }
}
