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

package com.nhnacademy.messenger.client.command;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;

@Slf4j
public class CommandFactory {

    private final Map<String, ClientCommand<?>> commands = new HashMap<>();

    // 로그인 없이 실행 가능한 명령어 목록 (화이트리스트)
    private final Set<String> publicCommands = new HashSet<>();

    public CommandFactory() {
        Reflections reflections = new Reflections("com.nhnacademy.messenger.client.command.impl");

        Set<Class<?>> annotatedClasses = reflections.getTypesAnnotatedWith(Command.class);

        for (Class<?> clazz : annotatedClasses) {
            registerCommand(clazz);
        }
    }

    private void registerCommand(Class<?> clazz) {
        try {
            Command commandAnnotation = clazz.getAnnotation(Command.class);

            String commandMethod = commandAnnotation.method();

            if (commandAnnotation.isPublic()) {
                publicCommands.add(commandMethod);
            }

            ClientCommand<?> commandInstance = (ClientCommand<?>) clazz.getDeclaredConstructor().newInstance();

            commands.put(commandMethod, commandInstance);

        } catch (Exception e) {
            log.error("커맨드 등록 실패 (Class: {}): {}", clazz.getName(), e.getMessage());
        }
    }

    public ClientCommand<?> getCommand(String commandName) {
        return commands.get(commandName);
    }

    public boolean isPublicCommand(String commandName) {
        return publicCommands.contains(commandName);
    }

}
