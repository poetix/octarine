/*
 * Copyright © 2017 VMware, Inc. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.codepoetics.octarine.bson.serialisation;


import org.bson.BsonArray;
import org.bson.BsonValue;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class BsonListSerialiser<T> implements SafeBsonSerialiser<Collection<? extends T>> {
    public static <T> BsonListSerialiser<T> writingItemsWith(BsonSerialiser<? super T> itemSerialiser) {
        return new BsonListSerialiser<>(itemSerialiser);
    }

    private final BsonSerialiser<? super T> itemSerialiser;

    private BsonListSerialiser(BsonSerialiser<? super T> itemSerialiser) {
        this.itemSerialiser = itemSerialiser;
    }

    @Override
    public BsonValue applyUnsafe(Collection<? extends T> values) throws IOException {
        List<BsonValue> bvalues = values.stream().map(v -> itemSerialiser.apply(v)).collect(Collectors.toList());
        return new BsonArray(bvalues);
    }
}
