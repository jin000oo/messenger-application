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
import com.nhnacademy.messenger.client.command.impl.ConsoleHelpCommand;
import com.nhnacademy.messenger.client.command.impl.CreateRoomCommand;
import com.nhnacademy.messenger.client.command.impl.EnterRoomCommand;
import com.nhnacademy.messenger.client.command.impl.HistoryCommand;
import com.nhnacademy.messenger.client.command.impl.LeaveRoomCommand;
import com.nhnacademy.messenger.client.command.impl.LoginCommand;
import com.nhnacademy.messenger.client.command.impl.LogoutCommand;
import com.nhnacademy.messenger.client.command.impl.RoomListCommand;
import com.nhnacademy.messenger.client.command.impl.UserListCommand;
import com.nhnacademy.messenger.client.command.impl.WhisperCommand;
import com.nhnacademy.messenger.client.ui.ClientUI;
import java.util.HashMap;
import java.util.Map;

public class CommandFactory {

    private final Map<String, ClientCommand> commands = new HashMap<>();

    public CommandFactory(ClientUI clientUI) {
        commands.put("/help", new ConsoleHelpCommand());
        commands.put("/login", new LoginCommand(clientUI));
        commands.put("/logout", new LogoutCommand(clientUI));
        commands.put("/users", new UserListCommand(clientUI));
        commands.put("/chat", new ChatCommand(clientUI));
        commands.put("/whisper", new WhisperCommand(clientUI));
        commands.put("/create", new CreateRoomCommand(clientUI));
        commands.put("/list", new RoomListCommand(clientUI));
        commands.put("/enter", new EnterRoomCommand(clientUI));
        commands.put("/leave", new LeaveRoomCommand(clientUI));
        commands.put("/history", new HistoryCommand(clientUI));
    }

    public ClientCommand getCommand(String commandName) {
        return commands.get(commandName);
    }

}
