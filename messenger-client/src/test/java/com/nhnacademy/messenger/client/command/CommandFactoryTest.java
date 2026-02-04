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

package com.nhnacademy.messenger.client.command;

import com.nhnacademy.messenger.client.command.impl.ChatCommand;
import com.nhnacademy.messenger.client.command.impl.LoginCommand;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class CommandFactoryTest {

    CommandFactory factory = new CommandFactory();

    @Test
    @DisplayName("명령어 키워드에 맞는 커맨드 객체 반환 확인")
    void getCommandTest() {
        Assertions.assertInstanceOf(LoginCommand.class, factory.getCommand("/login"));
        Assertions.assertInstanceOf(ChatCommand.class, factory.getCommand("/chat"));
        Assertions.assertNull(factory.getCommand("/invalid"));
    }

    @Test
    @DisplayName("로그인 없이 사용 가능한 명령어 확인 (Public Commands)")
    void publicCommandTest() {
        Assertions.assertTrue(factory.isPublicCommand("/login"));
        Assertions.assertTrue(factory.isPublicCommand("/help"));
        Assertions.assertFalse(factory.isPublicCommand("/chat"));
    }

}
