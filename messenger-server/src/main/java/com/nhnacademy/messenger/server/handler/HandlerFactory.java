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
import com.nhnacademy.messenger.server.handler.impl.*;
import com.nhnacademy.messenger.server.message.repository.MessageRepository;
import com.nhnacademy.messenger.server.message.repository.PrivateMessageRepository;
import com.nhnacademy.messenger.server.session.AuthService;
import com.nhnacademy.messenger.server.session.SessionRepository;
import com.nhnacademy.messenger.server.thread.MessageSender;
import com.nhnacademy.messenger.server.user.repository.UserRepository;

import java.util.EnumMap;
import java.util.Map;

public class HandlerFactory {

    private final Map<MessageType, Handler> handlers = new EnumMap<>(MessageType.class);

    private final UserRepository userRepo;
    private final ChatRoomRepository chatRoomRepo;
    private final MessageRepository messageRepo;
    private final PrivateMessageRepository privateMessageRepo;
    private final SessionRepository sessionRepo;
    private final AuthService authService;
    private final MessageSender sender;
    private final Handler unsupportedTypeHandler = new UnsupportedTypeHandler();

    public HandlerFactory(UserRepository userRepo,
                          ChatRoomRepository chatRoomRepo,
                          MessageRepository messageRepo,
                          PrivateMessageRepository privateMessageRepo,
                          SessionRepository sessionRepo,
                          AuthService authService,
                          MessageSender sender
    ) {
        this.userRepo = userRepo;
        this.chatRoomRepo = chatRoomRepo;
        this.messageRepo = messageRepo;
        this.privateMessageRepo = privateMessageRepo;
        this.sessionRepo = sessionRepo;
        this.authService = authService;
        this.sender = sender;

        initialize();
    }

    private void initialize() {
        handlers.put(MessageType.LOGIN, new LoginHandler(authService));
        handlers.put(MessageType.LOGOUT, new LogoutHandler(authService));
        handlers.put(MessageType.USER_LIST, new UserListHandler(userRepo, sessionRepo));

        handlers.put(MessageType.CHAT_MESSAGE, new ChatMessageHandler(userRepo, chatRoomRepo, messageRepo, sender));
        handlers.put(MessageType.PRIVATE_MESSAGE, new PrivateMessageHandler(userRepo, privateMessageRepo, sender));

        handlers.put(MessageType.CHAT_ROOM_CREATE, new ChatRoomCreateHandler(chatRoomRepo));
        handlers.put(MessageType.CHAT_ROOM_ENTER, new ChatRoomEnterHandler(chatRoomRepo));
        handlers.put(MessageType.CHAT_ROOM_EXIT, new ChatRoomExitHandler(chatRoomRepo));
        handlers.put(MessageType.CHAT_ROOM_LIST, new ChatRoomListHandler(chatRoomRepo));

        handlers.put(MessageType.CHAT_MESSAGE_HISTORY, new ChatMessageHistoryHandler(messageRepo));
    }

    public Handler getHandler(MessageType messageType) {
        return handlers.getOrDefault(messageType, unsupportedTypeHandler);
    }
}
