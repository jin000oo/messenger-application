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

package com.nhnacademy.messenger.server.handler.impl;

import com.nhnacademy.messenger.common.domain.ContentType;
import com.nhnacademy.messenger.common.domain.MessageRequest;
import com.nhnacademy.messenger.common.domain.MessageResponse;
import com.nhnacademy.messenger.common.domain.MessageType;
import com.nhnacademy.messenger.common.dto.message.ChatFileMessage;
import com.nhnacademy.messenger.common.dto.request.FileTransferRequest;
import com.nhnacademy.messenger.common.dto.response.FileTransferResponse;
import com.nhnacademy.messenger.server.chatroom.chatroomrepository.ChatRoomRepository;
import com.nhnacademy.messenger.server.chatroom.domain.ChatRoom;
import com.nhnacademy.messenger.server.handler.Handler;
import com.nhnacademy.messenger.server.handler.HandlerResult;
import com.nhnacademy.messenger.server.message.domain.ChatMessage;
import com.nhnacademy.messenger.server.message.repository.MessageRepository;
import com.nhnacademy.messenger.server.notification.NotificationService;
import com.nhnacademy.messenger.server.session.Session;
import com.nhnacademy.messenger.server.session.SessionRepository;
import com.nhnacademy.messenger.server.thread.MessageSender;
import com.nhnacademy.messenger.server.user.repository.UserRepository;
import com.nhnacademy.messenger.server.utils.IdGenerator;
import com.nhnacademy.messenger.server.utils.ResponseFactory;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.List;
import java.util.Objects;


//Long roomId,
//String fileName,
//Long fileSize,
//String fileData

@RequiredArgsConstructor
public class FileTransferHandler implements Handler {

    private static final long MAX_SIZE = 10L * 1024 * 1024;

    private final UserRepository userRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final MessageRepository messageRepository;
    private final SessionRepository sessionRepository;
    private final MessageSender sender;
    private final NotificationService notificationService;

    @Override
    public HandlerResult handle(MessageRequest<?> request) {
        if (request == null || request.getHeader() == null || request.getData() == null) {
            return ResponseFactory.error("COMMON.BAD_REQUEST", "데이터 형식이 올바르지 않습니다.");
        }

        if (!(request.getData() instanceof FileTransferRequest(
                Long roomId, String fileName, Long requestFileSize, String fileData
        )) || roomId == null || requestFileSize == null) {
            return ResponseFactory.error("COMMON.BAD_REQUEST", "데이터 형식이 올바르지 않습니다.");
        }

        if (StringUtils.isBlank(fileName) || requestFileSize < 0 || StringUtils.isBlank(fileData)) {
            return ResponseFactory.error("COMMON.BAD_REQUEST", "데이터 형식이 올바르지 않습니다.");
        }

        Session session = sessionRepository.getSession(request.getHeader().getSessionId());
        if (session == null) {
            return ResponseFactory.error("AUTH.INVALID_SESSION", "유효하지 않은 세션입니다.");
        }

        if (requestFileSize > MAX_SIZE) {
            return ResponseFactory.error("FILE.SIZE_EXCEEDED", "파일 크기 제한(10MB)을 초과했습니다.");
        }

        ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElse(null);
        if (chatRoom == null) {
            return ResponseFactory.error("ROOM.NOT_FOUND", "해당 채팅방을 찾을 수 없습니다.");
        }

        byte[] decodedFileData = {};
        try {
            decodedFileData = Base64.getDecoder().decode(fileData);
        } catch (IllegalArgumentException e) {
            return ResponseFactory.error("COMMON.BAD_REQUEST", "데이터 형식이 올바르지 않습니다.");
        }

        long fileSize = decodedFileData.length;
        if (fileSize > MAX_SIZE) {
            return ResponseFactory.error("FILE.SIZE_EXCEEDED", "파일 크기 제한(10MB)을 초과했습니다.");
        }

        long messageId = IdGenerator.nextMessageId();
        String senderId = session.getUserId();
        String senderName = userRepository.find(senderId)
                .map(user -> Objects.toString(user.getUserName(), ""))
                .orElse("");
        String now = Instant.now().truncatedTo(ChronoUnit.SECONDS).toString();

        messageRepository.save(new ChatMessage(
                messageId,
                roomId,
                senderId,
                senderName,
                now,
                fileData
        ));

        // 현재는 Send, Push 모두 한다.
        // MessageType.FILE_TRANSFER -> Send
        // MessageType.PUSH_NEW_MESSAGE -> Push
        // 나중에는 Push만 하도록 수정.
        List<String> members = chatRoom.getAllMembers().stream().toList();
        MessageResponse<ChatFileMessage> response = ResponseFactory.successResponse(
                MessageType.FILE_TRANSFER,
                new ChatFileMessage(senderId, fileName, fileSize, fileData)
        );

        sender.sendToUsers(members, response);

        return ResponseFactory.success(
                MessageType.FILE_TRANSFER_SUCCESS,
                new FileTransferResponse(roomId, messageId, fileName)
        ).addNotification(() -> notificationService.pushNewMessage(roomId, messageId, senderId, null, ContentType.FILE, fileName, fileSize));
    }
}
