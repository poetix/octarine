package com.codepoetics.octarine.json.deserialisation;

import com.codepoetics.octarine.records.Key;
import com.codepoetics.octarine.records.Record;
import com.codepoetics.octarine.records.Value;
import com.codepoetics.octarine.records.Schema;
import com.codepoetics.octarine.records.Valid;
import com.codepoetics.octarine.records.Validation;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import org.pcollections.PMap;
import org.pcollections.PVector;

import java.io.IOException;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public final class RecordDeserialiser implements SafeDeserialiser<Record> {

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder implements Supplier<RecordDeserialiser> {

        private final Map<String, Function<JsonParser, Value>> valueReaders = new HashMap<>();

        Builder() {
        }

        @Override
        public RecordDeserialiser get() {
            return new RecordDeserialiser((fieldName, parser) ->
                    Optional.ofNullable(valueReaders.get(fieldName)).map(r -> r.apply(parser)));
        }

        public <V> Builder read(Key<? super V> key, Function<JsonParser, ? extends V> deserialiser) {
            return read(key, key.name(), deserialiser);
        }

        public <V> Builder read(Key<? super V> key, String fieldName, Function<JsonParser, ? extends V> deserialiser) {
            valueReaders.put(fieldName, p -> key.of(deserialiser.apply(p)));
            return this;
        }

        public <V> Builder read(Key<V> key, Supplier<Function<JsonParser, V>> deserialiserSupplier) {
            return read(key, deserialiserSupplier.get());
        }

        public <V> Builder read(Key<V> key, String fieldName, Supplier<Function<JsonParser, V>> deserialiserSupplier) {
            return read(key, fieldName, deserialiserSupplier.get());
        }

        public Builder readString(Key<String> key) {
            return read(key, Deserialisers.ofString);
        }

        public <V> Builder readFromString(Key<? super V> key, Function<String, V> converter) {
            return read(key, Deserialisers.ofString.andThen(converter));
        }

        public Builder readInteger(Key<Integer> key) {
            return read(key, Deserialisers.ofInteger);
        }

        public Builder readDouble(Key<Double> key) {
            return read(key, Deserialisers.ofDouble);
        }

        public Builder readBoolean(Key<Boolean> key) {
            return read(key, Deserialisers.ofBoolean);
        }

        public Builder readLong(Key<Long> key) {
            return read(key, Deserialisers.ofLong);
        }

        public <V> Builder readList(Key<PVector<V>> key, Function<JsonParser, ? extends V> deserialiser) {
            return read(key, ListDeserialiser.readingItemsWith(deserialiser));
        }

        public <V> Builder readMap(Key<PMap<String, V>> key, Function<JsonParser, ? extends V> deserialiser) {
            return read(key, MapDeserialiser.readingValuesWith(deserialiser));
        }

        public <V> Builder readValidRecord(Key<Valid<V>> key, Function<JsonParser, Validation<V>> deserialiser) {
            return read(key, Deserialisers.ofValid(deserialiser));
        }

        public Builder readString(Key<String> key, String fieldName) {
            return read(key, fieldName, Deserialisers.ofString);
        }

        public <V> Builder readFromString(Key<? super V> key, String fieldName, Function<String, V> converter) {
            return read(key, fieldName, Deserialisers.ofString.andThen(converter));
        }

        public Builder readInteger(Key<Integer> key, String fieldName) {
            return read(key, fieldName, Deserialisers.ofInteger);
        }

        public Builder readDouble(Key<Double> key, String fieldName) {
            return read(key, fieldName, Deserialisers.ofDouble);
        }

        public Builder readBoolean(Key<Boolean> key, String fieldName) {
            return read(key, fieldName, Deserialisers.ofBoolean);
        }

        public Builder readLong(Key<Long> key, String fieldName) {
            return read(key, fieldName, Deserialisers.ofLong);
        }

        public <V> Builder readList(Key<PVector<V>> key, String fieldName, Function<JsonParser, ? extends V> deserialiser) {
            return read(key, fieldName, ListDeserialiser.readingItemsWith(deserialiser));
        }

        public <V> Builder readMap(Key<PMap<String, V>> key, String fieldName, Function<JsonParser, ? extends V> deserialiser) {
            return read(key, fieldName, MapDeserialiser.readingValuesWith(deserialiser));
        }

        public <V> Builder readValidRecord(Key<Valid<V>> key, String fieldName, Function<JsonParser, Validation<V>> deserialiser) {
            return read(key, fieldName, Deserialisers.ofValid(deserialiser));
        }
    }

    private final BiFunction<String, JsonParser, Optional<Value>> parserMapper;

    private RecordDeserialiser(BiFunction<String, JsonParser, Optional<Value>> parserMapper) {
        this.parserMapper = parserMapper;
    }

    @Override
    public Record applyUnsafe(JsonParser parser) throws IOException {
        List<Value> values = new ArrayList<>();

        if (parser.nextToken() == JsonToken.END_OBJECT) {
            return Record.empty();
        }

        /*
         * When the parser has hit the end of input nextToken() will always return null.
         * So need to prevent infinite loops we check the parser closed flag.
         */
        while (!parser.isClosed() && (parser.nextValue() != JsonToken.END_OBJECT)) {
            String fieldName = parser.getCurrentName();
            Optional<Value> result = parserMapper.apply(fieldName, parser);
            if (result.isPresent()) {
                values.add(result.get());
            } else {
                consumeUnwanted(parser);
            }
        }

        return Record.of(values);
    }

    private void consumeUnwanted(JsonParser parser) throws IOException {
        if (parser.getCurrentToken() == JsonToken.START_OBJECT) {
            while (parser.nextValue() != JsonToken.END_OBJECT) {
                if (parser.getCurrentToken() == JsonToken.START_OBJECT) {
                    consumeUnwanted(parser);
                }
            }
        }
    }

    public <S> Deserialiser<Validation<S>> validAgainst(Schema<S> schema) {
        return new ValidRecordDeserialiser<>(schema, this);
    }

}
