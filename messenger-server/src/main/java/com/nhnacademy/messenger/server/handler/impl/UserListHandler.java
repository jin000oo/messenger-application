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

import com.nhnacademy.messenger.common.domain.MessageRequest;
import com.nhnacademy.messenger.common.domain.MessageResponse;
import com.nhnacademy.messenger.common.domain.MessageType;
import com.nhnacademy.messenger.common.dto.response.UserListResponse;
import com.nhnacademy.messenger.common.dto.response.info.UserInfo;
import com.nhnacademy.messenger.server.handler.Handler;
import com.nhnacademy.messenger.server.user.repository.UserRepository;
import com.nhnacademy.messenger.server.utils.ResponseFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class UserListHandler implements Handler {

    private final UserRepository userRepository;

    @Override
    public MessageResponse<?> handle(MessageRequest<?> request) {
        if (request == null || request.getHeader() == null) {
            return ResponseFactory.error("COMMON.BAD_REQUEST", "데이터 형식이 올바르지 않습니다.");
        }

        List<UserInfo> userList = userRepository.findAll().stream()
                .map(user -> new UserInfo(user.getUserId(), user.getUserName(), user.isOnline()))
                .toList();

        return ResponseFactory.success(
                MessageType.USER_LIST_SUCCESS,
                new UserListResponse(userList)
        );
    }
}
