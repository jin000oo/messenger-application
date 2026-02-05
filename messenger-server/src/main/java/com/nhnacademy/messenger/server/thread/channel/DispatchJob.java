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

package com.nhnacademy.messenger.server.thread.channel;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.messenger.common.domain.MessageRequest;
import com.nhnacademy.messenger.common.domain.MessageResponse;
import com.nhnacademy.messenger.server.handler.MessageDispatcher;
import com.nhnacademy.messenger.server.thread.MessageSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.net.Socket;

@Slf4j
@RequiredArgsConstructor
public class DispatchJob implements Job {

    private final Socket socket;
    private final MessageRequest<?> request;
    private final MessageDispatcher dispatcher;
    private final MessageSender sender;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void execute() {
        MessageResponse<?> response = dispatcher.dispatch(request, socket);
        sender.send(socket, response);
    }
}
