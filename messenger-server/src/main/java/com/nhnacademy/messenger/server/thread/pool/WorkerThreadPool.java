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

package com.nhnacademy.messenger.server.thread.pool;

import com.nhnacademy.messenger.server.thread.channel.NotificationChannel;
import com.nhnacademy.messenger.server.thread.channel.RequestChannel;

import java.util.ArrayList;
import java.util.List;

public class WorkerThreadPool {

    private final List<Thread> threads = new ArrayList<>();
    private final List<Thread> notificationThreads = new ArrayList<>();

    public WorkerThreadPool(int poolSize, RequestChannel requestChannel, NotificationChannel notificationChannel) {
        for (int i = 0; i < poolSize; i++) {
            threads.add(new Thread(new RequestWorker(requestChannel)));
        }
        notificationThreads.add(new Thread(new NotificationWorker(notificationChannel)));
    }

    public void start() {
        for (Thread thread : threads) {
            thread.start();
        }
        for (Thread thread : notificationThreads) {
            thread.start();
        }
    }

    public void stop() {
        for (Thread thread : threads) {
            thread.interrupt();
        }
        for (Thread thread : notificationThreads) {
            thread.interrupt();
        }
    }
}
