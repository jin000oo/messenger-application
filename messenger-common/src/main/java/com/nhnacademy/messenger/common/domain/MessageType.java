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

package com.nhnacademy.messenger.common.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum MessageType {

    // Request
    @JsonProperty("LOGIN")
    LOGIN,  // 로그인

    @JsonProperty("LOGOUT")
    LOGOUT, // 로그아웃

    @JsonProperty("USER-LIST")
    USER_LIST,  // 모든 사용자 목록

    @JsonProperty("CHAT-MESSAGE")
    CHAT_MESSAGE,   // 채팅 메시지 전송

    @JsonProperty("PRIVATE-MESSAGE")
    PRIVATE_MESSAGE,    // 귓속말 전송

    @JsonProperty("CHAT-ROOM-CREATE")
    CHAT_ROOM_CREATE,   // 채팅방 생성

    @JsonProperty("CHAT-ROOM-LIST")
    CHAT_ROOM_LIST, // 채팅방 목록 조회

    @JsonProperty("CHAT-ROOM-ENTER")
    CHAT_ROOM_ENTER,    // 채팅방 입장

    @JsonProperty("CHAT-ROOM-EXIT")
    CHAT_ROOM_EXIT, // 채팅방 나가기

    @JsonProperty("CHAT-MESSAGE-HISTORY")
    CHAT_MESSAGE_HISTORY,   // 메시지 기록 조회


    // Response
    @JsonProperty("LOGIN-SUCCESS")
    LOGIN_SUCCESS,  // 로그인

    @JsonProperty("LOGOUT-SUCCESS")
    LOGOUT_SUCCESS, // 로그아웃

    @JsonProperty("USER-LIST-SUCCESS")
    USER_LIST_SUCCESS,  // 모든 사용자 목록

    @JsonProperty("CHAT-MESSAGE-SUCCESS")
    CHAT_MESSAGE_SUCCESS,   // 채팅 메시지 전송

    @JsonProperty("PRIVATE-MESSAGE-SUCCESS")
    PRIVATE_MESSAGE_SUCCESS,    // 귓속말 전송

    @JsonProperty("CHAT-ROOM-CREATE-SUCCESS")
    CHAT_ROOM_CREATE_SUCCESS,   // 채팅방 생성

    @JsonProperty("CHAT-ROOM-LIST-SUCCESS")
    CHAT_ROOM_LIST_SUCCESS, // 채팅방 목록 조회

    @JsonProperty("CHAT-ROOM-ENTER-SUCCESS")
    CHAT_ROOM_ENTER_SUCCESS,    // 채팅방 입장

    @JsonProperty("CHAT-ROOM-EXIT-SUCCESS")
    CHAT_ROOM_EXIT_SUCCESS, // 채팅방 나가기

    @JsonProperty("CHAT-MESSAGE-HISTORY-SUCCESS")
    CHAT_MESSAGE_HISTORY_SUCCESS,   // 메시지 기록 조회


    // Notification
    @JsonProperty("PUSH-NEW-MESSAGE")
    PUSH_NEW_MESSAGE, // 새 메시지 알림

    @JsonProperty("PUSH-ROOM-ENTER")
    PUSH_ROOM_ENTER, // 채팅방 입장 알림

    @JsonProperty("PUSH-ROOM-EXIT")
    PUSH_ROOM_EXIT, // 채팅방 퇴장 알림


    // Error
    @JsonProperty("ERROR")
    ERROR

}
