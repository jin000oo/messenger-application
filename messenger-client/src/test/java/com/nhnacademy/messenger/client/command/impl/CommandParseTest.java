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

import com.nhnacademy.messenger.client.dto.WhisperParams;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class CommandParseTest {

    @Test
    @DisplayName("WhisperCommand: 메시지에 공백이 있어도 잘 합쳐지는지 테스트")
    void whisperParseTest() {
        WhisperCommand command = new WhisperCommand();
        String[] args = {"/whisper", "marco", "hello", "world", "this", "is", "test"};

        WhisperParams params = command.parse(args);

        Assertions.assertEquals("marco", params.targetId());
        Assertions.assertEquals("hello world this is test", params.message());
    }

    @Test
    @DisplayName("LoginCommand: 인자가 부족하면 예외 발생")
    void loginParseFailTest() {
        LoginCommand command = new LoginCommand();
        String[] args = {"/login", "marco"};

        Assertions.assertThrows(IllegalArgumentException.class, () ->
                command.parse(args));
    }

    @Test
    @DisplayName("ChatCommand: 메시지가 비어있으면 예외 발생")
    void chatParseFailTest() {
        ChatCommand command = new ChatCommand();
        String[] args = {"/chat", "   "};

        Assertions.assertThrows(IllegalArgumentException.class, () ->
                command.parse(args));
    }

}
