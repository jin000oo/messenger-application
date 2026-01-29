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

package com.nhnacademy.messenger.client.command.impl;

import com.nhnacademy.messenger.client.command.ClientCommand;
import com.nhnacademy.messenger.common.domain.MessageRequest;
import com.nhnacademy.messenger.common.domain.MessageType;
import com.nhnacademy.messenger.common.util.MessageUtils;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.util.Map;

public class LoginCommand implements ClientCommand {

    @Override
    public void execute(String[] args, OutputStream out) {
        // 입력값 검증
        if (args.length < 3) {
            System.out.println("[Client] 사용법: /login <id> <password>");
            return;
        }

        String userId = args[1];
        String password = args[2];

        MessageRequest request = new MessageRequest(
                new MessageRequest.RequestHeader(MessageType.LOGIN, LocalDateTime.now().toString(), null),
                Map.of("userId", userId, "password", password));

        try {
            MessageUtils.send(out, request);
            System.out.println("[Client] 로그인 요청 전송 성공");

        } catch (IOException e) {
            System.out.printf("[Client] 로그인 요청 전송 실패: %s%s", e.getMessage(), System.lineSeparator());
        }
    }

}
