/*
 * Copyright © 2017 VMware, Inc. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.codepoetics.octarine.bson.deserialisation;


import org.bson.BsonArray;
import org.bson.BsonInvalidOperationException;
import org.bson.BsonValue;
import org.pcollections.PVector;
import org.pcollections.TreePVector;

import java.util.List;
import java.util.stream.Collectors;

public class BsonListDeserialiser<V> implements SafeBsonDeserialiser<PVector<V>> {
    public static <V> BsonListDeserialiser<V> readingItemsWith(BsonDeserialiser<? extends V> itemDeserialiser) {
        return new BsonListDeserialiser<>(itemDeserialiser);
    }

    private final BsonDeserialiser<? extends V> itemDeserialiser;

    private BsonListDeserialiser(BsonDeserialiser<? extends V> itemSerialiser) {
        this.itemDeserialiser = itemSerialiser;
    }

    @Override
    public PVector<V> applyUnsafe(BsonValue p) throws BsonInvalidOperationException {
        BsonArray bsonArray = p.asArray();
        List<V> values = bsonArray.getValues().stream().map(v -> itemDeserialiser.apply(v)).collect(Collectors.toList());
        return TreePVector.from(values);
    }
}