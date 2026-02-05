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

import com.nhnacademy.messenger.common.domain.MessageResponse;
import com.nhnacademy.messenger.common.domain.MessageType;
import com.nhnacademy.messenger.common.dto.response.ErrorResponse;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class ResponseFactory {

    public static <T> MessageResponse<T> success(MessageType messageType, T data) {
        return new MessageResponse<>(
                new MessageResponse.ResponseHeader(messageType, now(), true),
                data
        );
    }

    public static MessageResponse<ErrorResponse> error(String code, String message) {
        return new MessageResponse<>(
                new MessageResponse.ResponseHeader(MessageType.ERROR, now(), false),
                new ErrorResponse(code, message)
        );
    }

    private static String now() {
        return Instant.now().truncatedTo(ChronoUnit.SECONDS).toString();
    }
}
