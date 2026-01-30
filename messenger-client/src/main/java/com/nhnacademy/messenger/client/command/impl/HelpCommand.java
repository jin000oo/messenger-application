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
import java.io.OutputStream;

public class HelpCommand implements ClientCommand {

    @Override
    public void execute(String[] args, OutputStream out) {
        System.out.println("========== [ 명령어 목록 ] ==========");
        System.out.println("/login <id> <pw>");
        System.out.println("/logout");
        System.out.println("/users");
        System.out.println("/chat <message>");
        System.out.println("/whisper <target-id> <message>");
        System.out.println("/create <room-name>");
        System.out.println("/list");
        System.out.println("/enter <room-id>");
        System.out.println("/leave");
        System.out.println("/history");
        System.out.println("==================================");
    }

}
