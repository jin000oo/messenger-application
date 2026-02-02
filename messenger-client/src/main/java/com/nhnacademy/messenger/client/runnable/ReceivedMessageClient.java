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
import com.nhnacademy.messenger.common.util.MessageUtils;
import java.io.IOException;
import java.net.Socket;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ReceivedMessageClient implements Runnable {

    private final Socket socket;
    private final Subject subject;

    public ReceivedMessageClient(Socket socket, Subject subject) {
        if (socket == null) {
            throw new IllegalArgumentException("[ReceivedMessageClient] Socket이 null입니다.");
        }

        if (subject == null) {
            throw new IllegalArgumentException("[ReceivedMessageClient] Subject가 null입니다.");
        }

        this.socket = socket;
        this.subject = subject;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                MessageResponse response = MessageUtils.readResponse(socket.getInputStream());

                if (response == null) {
                    break;
                }

                subject.receiveMessage(response);

            } catch (IOException e) {
                System.out.printf("[Client] 예상치 못한 오류: %s\n", e.getMessage());
                throw new RuntimeException(e);
            }
        }
    }

}
