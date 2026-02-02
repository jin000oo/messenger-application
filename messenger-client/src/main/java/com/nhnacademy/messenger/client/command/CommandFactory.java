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
import com.nhnacademy.messenger.client.command.impl.HelpCommand;
import com.nhnacademy.messenger.client.command.impl.LoginCommand;
import com.nhnacademy.messenger.client.command.impl.LogoutCommand;
import java.util.HashMap;
import java.util.Map;

public class CommandFactory {

    private final Map<String, ClientCommand> commands = new HashMap<>();

    public CommandFactory() {
        commands.put("/help", new HelpCommand());
        commands.put("/login", new LoginCommand());
        commands.put("/logout", new LogoutCommand());
        commands.put("/chat", new ChatCommand());
    }

    public ClientCommand getCommand(String commandName) {
        return commands.get(commandName);
    }

}
