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
import com.nhnacademy.messenger.server.chatroom.chatroomrepository.ChatRoomRepository;
import com.nhnacademy.messenger.server.chatroom.chatroomrepository.impl.MemoryChatRoomRepository;
import com.nhnacademy.messenger.server.handler.impl.*;
import com.nhnacademy.messenger.server.message.repository.MessageRepository;
import com.nhnacademy.messenger.server.message.repository.PrivateMessageRepository;
import com.nhnacademy.messenger.server.message.repository.impl.MemoryMessageRepository;
import com.nhnacademy.messenger.server.message.repository.impl.MemoryPrivateMessageRepository;
import com.nhnacademy.messenger.server.user.repository.UserRepository;
import com.nhnacademy.messenger.server.user.repository.impl.MemoryUserRepository;

import java.util.EnumMap;
import java.util.Map;

public class HandlerFactory {

    private final Map<MessageType, Handler> handlers;

    private final UserRepository userRepo;
    private final ChatRoomRepository chatRoomRepo;
    private final MessageRepository messageRepo;
    private final PrivateMessageRepository privateMessageRepo;

    public HandlerFactory() {
        handlers = new EnumMap<>(MessageType.class);

        userRepo = new MemoryUserRepository();
        chatRoomRepo = new MemoryChatRoomRepository();
        messageRepo = new MemoryMessageRepository();
        privateMessageRepo = new MemoryPrivateMessageRepository();

        initialize();
    }

    private void initialize() {
        handlers.put(MessageType.LOGIN, new LoginHandler(userRepo));
        handlers.put(MessageType.LOGOUT, new LogoutHandler(userRepo));
        handlers.put(MessageType.USER_LIST, new UserListHandler(userRepo));

        handlers.put(MessageType.CHAT_MESSAGE, new ChatMessageHandler(userRepo, chatRoomRepo, messageRepo));
        handlers.put(MessageType.PRIVATE_MESSAGE, new PrivateMessageHandler(userRepo, privateMessageRepo));

        handlers.put(MessageType.CHAT_ROOM_CREATE, new ChatRoomCreateHandler(chatRoomRepo));
        handlers.put(MessageType.CHAT_ROOM_ENTER, new ChatRoomEnterHandler(chatRoomRepo));
        handlers.put(MessageType.CHAT_ROOM_EXIT, new ChatRoomExitHandler(chatRoomRepo));
        handlers.put(MessageType.CHAT_ROOM_LIST, new ChatRoomListHandler(chatRoomRepo));

        handlers.put(MessageType.CHAT_MESSAGE_HISTORY, new ChatMessageHistoryHandler(messageRepo));
    }

    public Handler getHandler(MessageType messageType) {
        return handlers.get(messageType);
    }
}
