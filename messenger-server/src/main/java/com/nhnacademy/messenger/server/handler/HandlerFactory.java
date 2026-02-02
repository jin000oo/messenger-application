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

package com.nhnacademy.messenger.server.handler;

import com.nhnacademy.messenger.common.domain.MessageType;
import com.nhnacademy.messenger.server.handler.impl.*;

import java.util.EnumMap;
import java.util.Map;

public class HandlerFactory {

    private static final Map<MessageType, Handler> Handlers = new EnumMap<>(MessageType.class);

    static {
        Handlers.put(MessageType.LOGIN, new LoginHandler());
        Handlers.put(MessageType.LOGOUT, new LogoutHandler());
        Handlers.put(MessageType.USER_LIST, new UserListHandler());
        Handlers.put(MessageType.PRIVATE_MESSAGE, new PrivateMessageHandler());
        Handlers.put(MessageType.CHAT_ROOM_CREATE, new ChatRoomCreateHandler());
        Handlers.put(MessageType.CHAT_ROOM_LIST, new ChatRoomListHandler());
        Handlers.put(MessageType.CHAT_ROOM_ENTER, new ChatRoomEnterHandler());
        Handlers.put(MessageType.CHAT_ROOM_EXIT, new ChatRoomExitHandler());
        Handlers.put(MessageType.CHAT_MESSAGE_HISTORY, new ChatMessageHistoryHandler());
    }

    public static Map<MessageType, Handler> getHandler() {
        return new EnumMap<MessageType, Handler>(Handlers);
    }
}
