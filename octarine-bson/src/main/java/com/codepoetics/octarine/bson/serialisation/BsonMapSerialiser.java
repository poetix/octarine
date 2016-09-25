/*
 * Copyright © 2017 VMware, Inc. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.codepoetics.octarine.bson.serialisation;


import org.bson.BsonDocument;
import org.bson.BsonValue;

import java.io.IOException;
import java.util.Map;

public class BsonMapSerialiser<T> implements SafeBsonSerialiser<Map<String, ? extends T>> {
    public static <T> BsonMapSerialiser<T> writingValuesWith(BsonSerialiser<? super T> valueSerialiser) {
        return new BsonMapSerialiser<>(valueSerialiser);
    }

    private final BsonSerialiser<? super T> valueSerialiser;

    private BsonMapSerialiser(BsonSerialiser<? super T> valueSerialiser) {
        this.valueSerialiser = valueSerialiser;
    }

    @Override
    public BsonValue applyUnsafe(Map<String, ? extends T> values) throws IOException {
        final BsonDocument doc = new BsonDocument();
        for (Map.Entry<String, ? extends T> e : values.entrySet()) {
            doc.put(e.getKey(), valueSerialiser.apply(e.getValue()));
        }
        return doc;
    }
}
