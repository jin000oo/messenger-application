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
import com.nhnacademy.messenger.client.command.impl.CreateRoomCommand;
import com.nhnacademy.messenger.client.command.impl.EnterRoomCommand;
import com.nhnacademy.messenger.client.command.impl.HelpCommand;
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
        commands.put("/help", new HelpCommand());
        commands.put("/login", new LoginCommand());
        commands.put("/logout", new LogoutCommand());
        commands.put("/users", new UserListCommand());
        commands.put("/chat", new ChatCommand());
        commands.put("/whisper", new WhisperCommand());
        commands.put("/create", new CreateRoomCommand());
        commands.put("/list", new RoomListCommand());
        commands.put("/enter", new EnterRoomCommand());
        commands.put("/leave", new LeaveRoomCommand());
        commands.put("/history", new HistoryCommand());
    }

    // TODO: sessionId 확인하는 로직이 중복!!!!! > 화이트리스트/블랙리스트로 관리
    public ClientCommand getCommand(String commandName) {
        return commands.get(commandName);
    }

}
