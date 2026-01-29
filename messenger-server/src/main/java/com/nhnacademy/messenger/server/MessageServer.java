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

package com.nhnacademy.messenger.server;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

@Slf4j
public class MessageServer implements Runnable {

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(12345)) {
            while (true) {
                Socket client = serverSocket.accept();
                Thread clientThread = new Thread(new ClientHandler(client));
                clientThread.start();

                log.debug("client 접속 성공!");

            }
        } catch (IOException e) {
            log.debug("IOException 발생했습니다.");
        }
    }
}
