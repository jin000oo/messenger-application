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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class MessageTest {

    ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("Enum 매핑 잘 되는지 확인")
    void enumMappingTest() throws JsonProcessingException {
        String json = objectMapper.writeValueAsString(MessageType.USER_LIST);

        Assertions.assertEquals("\"USER-LIST\"", json);
    }

    @Test
    @DisplayName("객체가 원하는대로 잘 나오는지 확인")
    void objectTest() throws JsonProcessingException {
        MessageRequest request = new MessageRequest(
                new MessageRequest.RequestHeader(MessageType.LOGIN, "2024-01-09T12:00:00Z", "UUID"),
                Map.of("userId", "marco"));

        String json = objectMapper.writeValueAsString(request);

        Assertions.assertTrue(json.contains("\"type\":\"LOGIN\""));
        Assertions.assertTrue(json.contains("\"userId\":\"marco\""));
    }

}
