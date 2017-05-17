/*
 * Copyright © 2017 VMware, Inc. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.codepoetics.octarine.bson.serialisation;


import com.codepoetics.octarine.records.Key;
import com.codepoetics.octarine.records.Record;
import org.bson.BsonDocument;
import org.bson.BsonElement;
import org.bson.BsonValue;
import org.bson.types.ObjectId;
import org.pcollections.PVector;

import java.io.IOException;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;


public class BsonRecordSerialiser implements SafeBsonSerialiser<Record> {

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder implements Supplier<BsonRecordSerialiser> {
        private final Map<Key<?>, Function<?, BsonElement>> serialiserMap = new LinkedHashMap<>();

        public <V> Builder write(Key<? extends V> key, String fieldName, Function<? super V, BsonValue> serialiser) {
            Function<? extends V, BsonElement> keyValueWriter = (V value) ->
                    new BsonElement(fieldName, serialiser.apply(value));
            serialiserMap.put(key, keyValueWriter);
            return this;
        }

        public <V> Builder write(Key<? extends V> key, Function<? super V, BsonValue> serialiser) {
            return write(key, key.name(), serialiser);
        }

        public <V> Builder write(Key<? extends V> key, Supplier<Function<? super V, BsonValue>> serialiserSupplier) {
            return write(key, serialiserSupplier.get());
        }

        public <V> Builder write(Key<? extends V> key, String fieldName, Supplier<Function<? super V, BsonValue>> serialiserSupplier) {
            return write(key, fieldName, serialiserSupplier.get());
        }

        public Builder writeBoolean(Key<Boolean> key) {
            return write(key, BsonSerialisers.toBoolean);
        }

        public Builder writeBoolean(Key<Boolean> key, String fieldName) {
            return write(key, fieldName, BsonSerialisers.toBoolean);
        }

        public Builder writeDate(Key<Date> key) {
            return write(key, BsonSerialisers.toDate);
        }

        public Builder writeDate(Key<Date> key, String fieldName) {
            return write(key, fieldName, BsonSerialisers.toDate);
        }

        public Builder writeDouble(Key<Double> key) {
            return write(key, BsonSerialisers.toDouble);
        }

        public Builder writeDouble(Key<Double> key, String fieldName) {
            return write(key, fieldName, BsonSerialisers.toDouble);
        }

        public Builder writeInteger(Key<Integer> key) {
            return write(key, BsonSerialisers.toInteger);
        }

        public Builder writeInteger(Key<Integer> key, String fieldName) {
            return write(key, fieldName, BsonSerialisers.toInteger);
        }

        public Builder writeLong(Key<Long> key) {
            return write(key, BsonSerialisers.toLong);
        }

        public Builder writeLong(Key<Long> key, String fieldName) {
            return write(key, fieldName, BsonSerialisers.toLong);
        }

        public Builder writeObjectId(Key<ObjectId> key) {
            return write(key, BsonSerialisers.toObjectId);
        }

        public Builder writeObjectId(Key<ObjectId> key, String fieldName) {
            return write(key, fieldName, BsonSerialisers.toObjectId);
        }

        public Builder writeString(Key<String> key) {
            return write(key, BsonSerialisers.toString);
        }

        public Builder writeString(Key<String> key, String fieldName) {
            return write(key, fieldName, BsonSerialisers.toString);
        }

        public <T> Builder writeList(Key<PVector<T>> key, BsonSerialiser<? super T> itemSerialiser) {
            return write(key, BsonListSerialiser.writingItemsWith(itemSerialiser));
        }

        public <T> Builder writeList(Key<PVector<T>> key, String fieldName, BsonSerialiser<? super T> itemSerialiser) {
            return write(key, fieldName, BsonListSerialiser.writingItemsWith(itemSerialiser));
        }

        public <V> Builder writeMap(Key<Map<String, V>> key, BsonSerialiser<V> valueSerialiser) {
            return write(key, BsonMapSerialiser.writingValuesWith(valueSerialiser));
        }

        public <V> Builder writeMap(Key<Map<String, V>> key, String fieldName, BsonSerialiser<V> valueSerialiser) {
            return write(key, fieldName, BsonMapSerialiser.writingValuesWith(valueSerialiser));
        }

        @Override
        public BsonRecordSerialiser get() {
            return new BsonRecordSerialiser(serialiserMap);
        }
    }

    private final Map<Key<?>, Function<?, BsonElement>> serialiserMap;

    public BsonRecordSerialiser(Map<Key<?>, Function<?, BsonElement>> serialiserMap) {
        this.serialiserMap = serialiserMap;
    }

    @Override
    public BsonDocument applyUnsafe(Record record) throws IOException {
        List<BsonElement> elems = serialiserMap.entrySet().stream()
                .filter(e -> e.getKey().get(record).isPresent())
                .map(e -> ((Function<Object, BsonElement>) serialiserMap.get(e.getKey())).apply(e.getKey().get(record).get()))
                .collect(Collectors.toList());
        return new BsonDocument(elems);
    }
}
