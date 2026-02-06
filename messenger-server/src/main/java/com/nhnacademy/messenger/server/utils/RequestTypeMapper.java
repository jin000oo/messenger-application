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

package com.nhnacademy.messenger.server.utils;

import com.nhnacademy.messenger.common.domain.MessageRequest;
import com.nhnacademy.messenger.common.domain.MessageType;
import com.nhnacademy.messenger.common.dto.request.*;
import com.nhnacademy.messenger.common.util.MessageUtils;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RequestTypeMapper {

    private final MessageUtils messageUtils;

    public <T> T convert(Object data, Class<T> clazz) {
        if (data == null) return null;

        try {
            return messageUtils.convertData(data, clazz);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public MessageRequest<?> toTyped(MessageRequest<?> raw) {
        if (raw == null || raw.getHeader() == null) return raw;

        MessageType type = raw.getHeader().getType();
        Object data = raw.getData();

        Object typedData = switch (type) {
            case LOGIN -> convert(data, LoginRequest.class);

            case CHAT_MESSAGE -> convert(data, ChatRequest.class);
            case PRIVATE_MESSAGE -> convert(data, WhisperRequest.class);

            case CHAT_ROOM_CREATE -> convert(data, CreateChatRoomRequest.class);
            case CHAT_ROOM_ENTER -> convert(data, EnterChatRoomRequest.class);
            case CHAT_ROOM_EXIT -> convert(data, LeaveChatRoomRequest.class);

            case CHAT_MESSAGE_HISTORY -> convert(data, HistoryRequest.class);

            case FILE_TRANSFER -> convert(data, FileTransferRequest.class);

            // LOGOUT, USER-LIST, CHAT-ROOM-LIST
            default -> data;
        };

        return new MessageRequest<>(raw.getHeader(), typedData);
    }
}
