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

package com.nhnacademy.messenger.common.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.nhnacademy.messenger.common.domain.MessageRequest;
import com.nhnacademy.messenger.common.domain.MessageResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class MessageUtils {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final String LENGTH_PREFIX = "message-length: ";

    static {
        objectMapper.registerModule(new JavaTimeModule());
    }

    // 객체를 JSON으로 변환 후 헤더를 붙여 전송
    public static void send(OutputStream out, Object message) throws IOException {
        // 객체 > JSON > byte 배열
        String json = objectMapper.writeValueAsString(message);
        byte[] bodyBytes = json.getBytes(StandardCharsets.UTF_8);

        // 헤더 생성
        String header = String.format("%s%d\n", LENGTH_PREFIX, bodyBytes.length);
        byte[] headerBytes = header.getBytes(StandardCharsets.UTF_8);

        // 전송 (헤더 + 바디)
        out.write(headerBytes);
        out.write(bodyBytes);
        out.flush();
    }

    // 클라이언트가 보낸 요청을 받을 때 사용
    public static MessageRequest readRequest(InputStream in) throws IOException {
        byte[] data = readStream(in);

        if (data == null) {
            return null;
        }

        return objectMapper.readValue(data, MessageRequest.class);
    }

    // 서버가 보낸 응답을 받을 때 사용
    public static MessageResponse readResponse(InputStream in) throws IOException {
        byte[] data = readStream(in);

        if (data == null) {
            return null;
        }

        return objectMapper.readValue(data, MessageResponse.class);
    }

    // 실제 스트림에서 바이트를 읽어오는 메소드
    private static byte[] readStream(InputStream in) throws IOException {
        // 헤더 읽기
        String headerLine = readLine(in);

        if (headerLine == null) {
            return null;
        }

        if (!headerLine.startsWith(LENGTH_PREFIX)) {
            throw new IOException("[MessageUtils] 잘못된 프로토콜: " + headerLine);
        }

        // 길이 파싱
        String lengthStr = headerLine.substring(LENGTH_PREFIX.length()).trim();
        int length = Integer.parseInt(lengthStr);

        // 바디 읽기
        byte[] bodyBytes = in.readNBytes(length);

        if (bodyBytes.length != length) {
            throw new IOException("[MessageUtils] 데이터 부족");
        }

        return bodyBytes;
    }

    // 한줄 읽기 (헬퍼 메소드)
    private static String readLine(InputStream in) throws IOException {
        StringBuilder sb = new StringBuilder();
        int line;

        while ((line = in.read()) != -1) {
            if (line == '\n') {
                break;
            }

            sb.append((char) line);
        }

        if (sb.isEmpty() && line == -1) {
            return null;
        }

        return sb.toString();
    }

}
