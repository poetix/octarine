/*
 * Copyright © 2017 VMware, Inc. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.codepoetics.octarine.bson.deserialisation;

import org.bson.BsonInvalidOperationException;

public class BsonDeserialisationException extends RuntimeException {
    public BsonDeserialisationException(BsonInvalidOperationException e) {
        super(e);
    }

    public BsonDeserialisationException(String msg) {
        super(msg);
    }
}
