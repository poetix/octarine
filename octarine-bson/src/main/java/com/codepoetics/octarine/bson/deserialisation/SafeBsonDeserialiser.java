/*
 * Copyright © 2017 VMware, Inc. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.codepoetics.octarine.bson.deserialisation;

import org.bson.BsonInvalidOperationException;
import org.bson.BsonValue;

public interface SafeBsonDeserialiser<S> extends BsonDeserialiser<S> {
    default S apply(BsonValue p) {
        try {
            return applyUnsafe(p);
        } catch (BsonInvalidOperationException e) {
            throw new BsonDeserialisationException(e);
        }
    }

    S applyUnsafe(BsonValue p) throws BsonInvalidOperationException;
}
