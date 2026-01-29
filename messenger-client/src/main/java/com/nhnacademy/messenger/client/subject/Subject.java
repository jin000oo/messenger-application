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

package com.nhnacademy.messenger.client.subject;

import com.nhnacademy.messenger.client.observer.Observer;

public interface Subject {

    void register(EventType eventType, Observer observer);

    void remove(EventType eventType, Observer observer);

    void notifyObservers(EventType eventType, String message);

    default void sendMessage(String message) {
        notifyObservers(EventType.SEND, message);
    }

    default void receiveMessage(String message) {
        notifyObservers(EventType.RECV, message);
    }

}
