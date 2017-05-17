/*
 * Copyright © 2017 VMware, Inc. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.codepoetics.octarine.bson.deserialisation;


import com.codepoetics.octarine.records.Key;
import com.codepoetics.octarine.records.ListKey;
import com.codepoetics.octarine.records.Record;
import com.codepoetics.octarine.records.Schema;
import com.codepoetics.octarine.records.Valid;
import com.codepoetics.octarine.records.Validation;
import com.codepoetics.octarine.records.Value;
import org.bson.BsonDocument;
import org.bson.BsonInvalidOperationException;
import org.bson.BsonValue;
import org.bson.types.ObjectId;
import org.pcollections.PMap;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class BsonRecordDeserialiser implements SafeBsonDeserialiser<Record> {

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder implements Supplier<BsonRecordDeserialiser> {

        private final Map<Key<?>, Function<Map<String, BsonValue>, Optional<Value>>> deserialiserMap = new LinkedHashMap<>();

        public <V> Builder read(Key<? super V> key, String fieldName, Function<BsonValue, ? extends V> deserialiser) {
            Function<Map<String, BsonValue>, Optional<Value>> keyValueReader = (m) -> {
                BsonValue v = m.get(fieldName);
                return (null == v) ? Optional.empty() : Optional.of(key.of(deserialiser.apply(v)));
            };
            deserialiserMap.put(key, keyValueReader);
            return this;
        }

        public <V> Builder read(Key<? super V> key, Function<BsonValue, ? extends V> deserialiser) {
            return read(key, key.name(), deserialiser);
        }

        public <V> Builder read(Key<V> key, Supplier<Function<BsonValue, V>> deserialiserSupplier) {
            return read(key, deserialiserSupplier.get());
        }

        public <V> Builder read(Key<V> key, String fieldName, Supplier<Function<BsonValue, V>> deserialiserSupplier) {
            return read(key, fieldName, deserialiserSupplier.get());
        }

        public Builder readBoolean(Key<Boolean> key) {
            return read(key, BsonDeserialisers.ofBoolean);
        }

        public Builder readBoolean(Key<Boolean> key, String fieldName) {
            return read(key, fieldName, BsonDeserialisers.ofBoolean);
        }

        public Builder readDate(Key<Date> key) {
            return read(key, BsonDeserialisers.ofDate);
        }

        public Builder readDate(Key<Date> key, String fieldName) {
            return read(key, fieldName, BsonDeserialisers.ofDate);
        }

        public Builder readDouble(Key<Double> key) {
            return read(key, BsonDeserialisers.ofDouble);
        }

        public Builder readDouble(Key<Double> key, String fieldName) {
            return read(key, fieldName, BsonDeserialisers.ofDouble);
        }

        public Builder readInteger(Key<Integer> key) {
            return read(key, BsonDeserialisers.ofInteger);
        }

        public Builder readInteger(Key<Integer> key, String fieldName) {
            return read(key, fieldName, BsonDeserialisers.ofInteger);
        }

        public Builder readLong(Key<Long> key) {
            return read(key, BsonDeserialisers.ofLong);
        }

        public Builder readLong(Key<Long> key, String fieldName) {
            return read(key, fieldName, BsonDeserialisers.ofLong);
        }

        public Builder readObjectId(Key<ObjectId> key) {
            return read(key, BsonDeserialisers.ofObjectId);
        }

        public Builder readObjectId(Key<ObjectId> key, String fieldName) {
            return read(key, fieldName, BsonDeserialisers.ofObjectId);
        }

        public Builder readString(Key<String> key) {
            return read(key, BsonDeserialisers.ofString);
        }

        public Builder readString(Key<String> key, String fieldName) {
            return read(key, fieldName, BsonDeserialisers.ofString);
        }

        public <V> Builder readList(ListKey<V> key, BsonDeserialiser<? extends V> itemDeserialiser) {
            return read(key, BsonListDeserialiser.readingItemsWith(itemDeserialiser));
        }

        public <V> Builder readList(ListKey<V> key, String fieldName, BsonDeserialiser<? extends V> itemDeserialiser) {
            return read(key, fieldName, BsonListDeserialiser.readingItemsWith(itemDeserialiser));
        }

        public <V> Builder readMap(Key<PMap<String, V>> key, BsonDeserialiser<? extends V> valueDeserialiser) {
            return read(key, BsonMapDeserialiser.readingValuesWith(valueDeserialiser));
        }

        public <V> Builder readMap(Key<PMap<String, V>> key, String fieldName, BsonDeserialiser<? extends V> valueDeserialiser) {
            return read(key, fieldName, BsonMapDeserialiser.readingValuesWith(valueDeserialiser));
        }

        public <V> Builder readValidRecord(Key<Valid<V>> key, Function<BsonValue, Validation<V>> deserialiser) {
            return read(key, BsonDeserialisers.ofValid(deserialiser));
        }

        @Override
        public BsonRecordDeserialiser get() {
            return new BsonRecordDeserialiser(deserialiserMap);
        }
    }

    private final Map<Key<?>, Function<Map<String, BsonValue>, Optional<Value>>> deserialiserMap;

    public BsonRecordDeserialiser(Map<Key<?>, Function<Map<String, BsonValue>, Optional<Value>>> deserialiserMap) {
        this.deserialiserMap = deserialiserMap;
    }

    @Override
    public Record applyUnsafe(BsonValue bsonValue) throws BsonInvalidOperationException {
        return Record.of(valuesFrom(bsonValue));
    }

    Stream<Value> valuesFrom(BsonValue bsonValue) throws BsonInvalidOperationException {
        BsonDocument doc = bsonValue.asDocument();
        return deserialiserMap.values().stream()
                .map(d -> d.apply(doc))
                .filter(Optional::isPresent)
                .map(Optional::get);
    }

    public <S> BsonDeserialiser<Validation<S>> validAgainst(Schema<S> schema) {
        return new BsonValidRecordDeserialiser<>(schema, this);
    }
}
