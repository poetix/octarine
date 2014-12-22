package com.codepoetics.octarine.json.serialisation;

import com.codepoetics.octarine.api.Key;
import com.codepoetics.octarine.api.Record;
import com.fasterxml.jackson.core.JsonGenerator;
import org.pcollections.PVector;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public final class RecordSerialiser implements SafeSerialiser<Record> {

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder implements Supplier<RecordSerialiser> {
        private final Map<Key<?>, BiConsumer<JsonGenerator, ?>> serialiserMap = new LinkedHashMap<>();

        public <V> Builder write(Key<? extends V> key, String fieldName, BiConsumer<JsonGenerator, ? super V> serialiser) {
            BiConsumer<JsonGenerator, ? extends V> keyValueWriter = (JsonGenerator generator, V value) -> {
                try {
                    generator.writeFieldName(fieldName);
                    serialiser.accept(generator, value);
                } catch (IOException e) {
                    throw new SerialisationException(e);
                }
            };
            serialiserMap.put(key, keyValueWriter);
            return this;
        }

        public <V> Builder write(Key<? extends V> key, BiConsumer<JsonGenerator, ? super V> serialiser) {
            return write(key, key.name(), serialiser);
        }

        public <V> Builder write(Key<? extends V> key, Supplier<BiConsumer<JsonGenerator, ? super V>> serialiserSupplier) {
            return write(key, serialiserSupplier.get());
        }

        public <V> Builder write(Key<? extends V> key, String fieldName, Supplier<BiConsumer<JsonGenerator, ? super V>> serialiserSupplier) {
            return write(key, fieldName, serialiserSupplier.get());
        }

        public Builder writeString(Key<String> key) {
            return write(key, Serialisers.toString);
        }

        public Builder writeString(Key<String> key, String fieldName) {
            return write(key, fieldName, Serialisers.toString);
        }

        public <V> Builder writeToString(Key<V> key) {
            return writeToString(key, Object::toString);
        }

        public <V> Builder writeToString(Key<V> key, String fieldName) {
            return writeToString(key, fieldName, Object::toString);
        }

        public <V> Builder writeToString(Key<V> key, Function<V, String> converter) {
            return writeToString(key, key.name(), converter);
        }

        public <V> Builder writeToString(Key<V> key, String fieldName, Function<V, String> converter) {
            return write(key, fieldName, (g, v) -> Serialisers.toString.accept(g, converter.apply(v)));
        }

        public Builder writeInteger(Key<Integer> key) {
            return write(key, Serialisers.toInteger);
        }

        public Builder writeInteger(Key<Integer> key, String fieldName) {
            return write(key, fieldName, Serialisers.toInteger);
        }

        public Builder writeDouble(Key<Double> key) {
            return write(key, Serialisers.toDouble);
        }

        public Builder writeDouble(Key<Double> key, String fieldName) {
            return write(key, fieldName, Serialisers.toDouble);
        }

        public Builder writeLong(Key<Long> key) {
            return write(key, Serialisers.toLong);
        }

        public Builder writeLong(Key<Long> key, String fieldName) {
            return write(key, fieldName, Serialisers.toLong);
        }


        public Builder writeBoolean(Key<Boolean> key) {
            return write(key, Serialisers.toBoolean);
        }

        public Builder writeBoolean(Key<Boolean> key, String fieldName) {
            return write(key, fieldName, Serialisers.toBoolean);
        }

        public <V> Builder writeList(Key<PVector<V>> key, Serialiser<V> itemSerialiser) {
            return write(key, ListSerialiser.writingItemsWith(itemSerialiser));
        }

        public <V> Builder writeList(Key<PVector<V>> key, String fieldName, Serialiser<V> itemSerialiser) {
            return write(key, fieldName, ListSerialiser.writingItemsWith(itemSerialiser));
        }

        public <V> Builder writeMap(Key<Map<String, V>> key, Serialiser<V> valueSerialiser) {
            return write(key, MapSerialiser.writingValuesWith(valueSerialiser));
        }

        public <V> Builder writeMap(Key<Map<String, V>> key, String fieldName, Serialiser<V> valueSerialiser) {
            return write(key, fieldName, MapSerialiser.writingValuesWith(valueSerialiser));
        }

        @Override
        public RecordSerialiser get() {
            return new RecordSerialiser(serialiserMap);
        }
    }

    private final Map<Key<?>, BiConsumer<JsonGenerator, ?>> serialiserMap;

    public RecordSerialiser(Map<Key<?>, BiConsumer<JsonGenerator, ?>> serialiserMap) {
        this.serialiserMap = serialiserMap;
    }

    @Override
    public void unsafeAccept(JsonGenerator generator, Record record) throws IOException {
        generator.writeStartObject();
        serialiserMap.keySet().forEach(k ->
                k.get(record).ifPresent(value ->
                        ((BiConsumer<JsonGenerator, Object>)  serialiserMap.get(k)).accept(generator, value)
        ));
        generator.writeEndObject();
    }


}
