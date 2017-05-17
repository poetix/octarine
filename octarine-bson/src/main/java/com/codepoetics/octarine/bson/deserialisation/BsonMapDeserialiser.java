/*
 * Copyright © 2017 VMware, Inc. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.codepoetics.octarine.bson.deserialisation;


import org.bson.BsonDocument;
import org.bson.BsonInvalidOperationException;
import org.bson.BsonValue;
import org.pcollections.HashTreePMap;
import org.pcollections.PMap;

import java.util.HashMap;
import java.util.Map;

public class BsonMapDeserialiser<T> implements SafeBsonDeserialiser<PMap<String, T>> {
    public static <T> BsonMapDeserialiser<T> readingValuesWith(BsonDeserialiser<? extends T> valueDeserialiser) {
        return new BsonMapDeserialiser<>(valueDeserialiser);
    }

    private final BsonDeserialiser<? extends T> valueDeserialiser;

    private BsonMapDeserialiser(BsonDeserialiser<? extends T> valueDeserialiser) {
        this.valueDeserialiser = valueDeserialiser;
    }

    @Override
    public PMap<String, T> applyUnsafe(BsonValue p) throws BsonInvalidOperationException {
        BsonDocument doc = p.asDocument();
        Map<String, T> values = new HashMap<>();
        for (Map.Entry<String,BsonValue> e : doc.entrySet()) {
            values.put(e.getKey(), valueDeserialiser.apply(e.getValue()));
        }

        return HashTreePMap.from(values);
    }
}
