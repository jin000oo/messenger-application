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
import com.nhnacademy.messenger.server.notification.NotificationService;
import com.nhnacademy.messenger.server.session.SessionRepository;
import com.nhnacademy.messenger.server.session.SessionService;
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
    private final SessionService sessionService;
    private final MessageSender sender;
    private final NotificationService notificationService;
    private final Handler unsupportedTypeHandler = new UnsupportedTypeHandler();

    public HandlerFactory(UserRepository userRepo,
                          ChatRoomRepository chatRoomRepo,
                          MessageRepository messageRepo,
                          PrivateMessageRepository privateMessageRepo,
                          SessionRepository sessionRepo,
                          SessionService sessionService,
                          MessageSender sender,
                          NotificationService notificationService
    ) {
        this.userRepo = userRepo;
        this.chatRoomRepo = chatRoomRepo;
        this.messageRepo = messageRepo;
        this.privateMessageRepo = privateMessageRepo;
        this.sessionRepo = sessionRepo;
        this.sessionService = sessionService;
        this.sender = sender;
        this.notificationService = notificationService;

        initialize();
    }

    private void initialize() {
        handlers.put(MessageType.LOGIN, new LoginHandler(userRepo, sessionService));
        handlers.put(MessageType.LOGOUT, new LogoutHandler(userRepo, sessionService));
        handlers.put(MessageType.USER_LIST, new UserListHandler(userRepo));

        handlers.put(MessageType.CHAT_MESSAGE, new ChatMessageHandler(userRepo, chatRoomRepo, messageRepo, sessionRepo, sender, notificationService));
        handlers.put(MessageType.PRIVATE_MESSAGE, new PrivateMessageHandler(userRepo, privateMessageRepo, sessionRepo, sender));

        handlers.put(MessageType.CHAT_ROOM_CREATE, new ChatRoomCreateHandler(chatRoomRepo));
        handlers.put(MessageType.CHAT_ROOM_ENTER, new ChatRoomEnterHandler(chatRoomRepo, sessionRepo, notificationService));
        handlers.put(MessageType.CHAT_ROOM_EXIT, new ChatRoomExitHandler(chatRoomRepo, sessionRepo, notificationService));
        handlers.put(MessageType.CHAT_ROOM_LIST, new ChatRoomListHandler(chatRoomRepo));

        handlers.put(MessageType.CHAT_MESSAGE_HISTORY, new ChatMessageHistoryHandler(messageRepo));

        handlers.put(MessageType.FILE_TRANSFER, new FileTransferHandler(userRepo, chatRoomRepo, messageRepo, sessionRepo, sender, notificationService));
    }

    public Handler getHandler(MessageType messageType) {
        return handlers.getOrDefault(messageType, unsupportedTypeHandler);
    }
}
