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
import com.nhnacademy.messenger.client.command.Command;
import com.nhnacademy.messenger.client.context.ClientContext;

@Command(method = "/help", isPublic = true)
public class HelpCommand implements ClientCommand<Void> {

    @Override
    public Void parse(String[] args) {
        return null;
    }

    @Override
    public void execute(Void params, ClientContext context) {
        StringBuilder sb = new StringBuilder();
        sb.append("========== [ 명령어 목록 ] ==========\n");
        sb.append("/login <id> <pw>\n");
        sb.append("/logout\n");
        sb.append("/users\n");
        sb.append("/chat <message>\n");
        sb.append("/whisper <target-id> <message>\n");
        sb.append("/create <room-name>\n");
        sb.append("/list\n");
        sb.append("/enter <room-id>\n");
        sb.append("/leave\n");
        sb.append("/history\n");
        sb.append("==================================");

        context.getClientUI().displayMessage(sb.toString());
    }

}
