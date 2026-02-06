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

package com.nhnacademy.messenger.server.handler;

import com.nhnacademy.messenger.common.domain.MessageResponse;
import com.nhnacademy.messenger.server.thread.channel.Job;
import com.nhnacademy.messenger.server.thread.channel.impl.NotificationJob;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class HandlerResult {

    private final MessageResponse<?> response;
    private final List<Job> tasks = new ArrayList<>();

    public HandlerResult(MessageResponse<?> response) {
        this.response = response;
    }

    public HandlerResult addNotification(Runnable task) {
        if (task != null) {
            tasks.add(new NotificationJob(task));
        }

        return this;
    }
}
