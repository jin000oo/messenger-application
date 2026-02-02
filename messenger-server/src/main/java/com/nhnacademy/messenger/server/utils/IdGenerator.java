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

import java.util.concurrent.ThreadLocalRandom;

public class IdGenerator {

    public static long randomRoomIdGenerator() {
        return ThreadLocalRandom.current().nextLong(10_000_000L, 100_000_000L);
    }

    public static long randomMessageIdGenerator() {
        return ThreadLocalRandom.current().nextLong(1_000_000_000L, 10_000_000_000L);
    }
}
