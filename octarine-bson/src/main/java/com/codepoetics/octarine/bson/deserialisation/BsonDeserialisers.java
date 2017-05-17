/*
 * Copyright © 2017 VMware, Inc. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.codepoetics.octarine.bson.deserialisation;


import com.codepoetics.octarine.records.Valid;
import com.codepoetics.octarine.records.Validation;
import org.bson.BsonValue;
import org.bson.types.ObjectId;

import java.util.Date;
import java.util.function.Function;

public interface BsonDeserialisers {

    SafeBsonDeserialiser<Boolean> ofBoolean = (v) -> v.asBoolean().getValue();

    SafeBsonDeserialiser<Date> ofDate = (v) -> new Date(v.asDateTime().getValue());

    SafeBsonDeserialiser<Double> ofDouble = (v) -> v.asDouble().getValue();

    SafeBsonDeserialiser<Integer> ofInteger = (v) -> v.asInt32().getValue();

    SafeBsonDeserialiser<Long> ofLong = (v) -> v.asInt64().getValue();

    SafeBsonDeserialiser<ObjectId> ofObjectId = (v) -> v.asObjectId().getValue();

    SafeBsonDeserialiser<String> ofString = (v) -> v.asString().getValue();

    static <V> SafeBsonDeserialiser<Valid<V>> ofValid(Function<BsonValue, ? extends Validation<V>> deserialiser) {
        return parser -> deserialiser.apply(parser).get();
    }
}
