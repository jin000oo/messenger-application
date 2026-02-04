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

package com.nhnacademy.messenger.client.runnable;

import com.nhnacademy.messenger.client.subject.Subject;
import com.nhnacademy.messenger.common.domain.MessageResponse;
import java.util.concurrent.BlockingQueue;

public class MessageProcessor implements Runnable {

    private final BlockingQueue<MessageResponse> messageQueue;
    private final Subject subject;

    public MessageProcessor(BlockingQueue<MessageResponse> messageQueue, Subject subject) {
        this.messageQueue = messageQueue;
        this.subject = subject;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                MessageResponse response = messageQueue.take();
                subject.receiveMessage(response);

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

}
