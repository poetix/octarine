/*
 * Copyright © 2017 VMware, Inc. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.codepoetics.octarine.bson.serialisation;

import org.bson.BsonValue;

import java.io.IOException;

public interface SafeBsonSerialiser<T> extends BsonSerialiser<T> {
    default BsonValue apply(T value) {
        try {
            return applyUnsafe(value);
        } catch (IOException e) {
            throw new BsonSerialisationException(e);
        }
    }

    BsonValue applyUnsafe(T value) throws IOException;
}
