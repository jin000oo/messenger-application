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

import com.nhnacademy.messenger.client.ui.ClientUI;
import com.nhnacademy.messenger.common.domain.MessageResponse;
import com.nhnacademy.messenger.common.util.MessageUtils;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ReceivedMessageClient implements Runnable {

    private final Socket socket;
    private final BlockingQueue<MessageResponse> messageQueue;
    private final ClientUI clientUI;

    private final MessageUtils messageUtils;

    public ReceivedMessageClient(Socket socket, BlockingQueue<MessageResponse> messageQueue, ClientUI clientUI,
                                 MessageUtils messageUtils) {
        this.socket = socket;
        this.messageQueue = messageQueue;
        this.clientUI = clientUI;
        this.messageUtils = messageUtils;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                MessageResponse response = messageUtils.readResponse(socket.getInputStream());

                if (response == null) {
                    break;
                }

                messageQueue.put(response);

            } catch (IOException e) {
                clientUI.displayMessage(String.format("예상치 못한 오류: %s", e.getMessage()));
                break;

            } catch (InterruptedException e) {
                break;
            }
        }
    }

}
