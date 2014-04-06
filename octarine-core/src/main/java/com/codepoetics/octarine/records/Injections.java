package com.codepoetics.octarine.records;

import org.pcollections.HashTreePMap;
import org.pcollections.PVector;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public interface Injections<T> extends Supplier<RecordBuilder<T>> {

    static <T> Injections<T> against(Deserialiser<T> deserialiser) {
        Map<String, Consumer<T>> fieldDeserialisers = new HashMap<>();
        Map<Key<?>, Object> values = new HashMap<>();

        return new Injections<T>() {
            @Override
            public <V> Injections<T> add(Key<V> key, String fieldName, Function<T, V> extractor) {
                fieldDeserialisers.put(fieldName, p -> values.put(key, extractor.apply(p)));
                return this;
            }

            @Override
            public RecordBuilder<T> get() {
                return new RecordBuilder<T>() {
                    @Override
                    public void accept(String fieldName, T parser) {
                        Optional<Consumer<T>> maybeDeserialiser = Optional.ofNullable(fieldDeserialisers.get(fieldName));
                        maybeDeserialiser.ifPresent(fd -> fd.accept(parser));
                    }

                    @Override
                    public Record get() {
                        return Record.of(HashTreePMap.from(values));
                    }
                };
            }

            @Override
            public <V> Function<T, PVector<V>> fromList(Function<T, V> extractor) {
                return reader -> deserialiser.readList(reader, extractor);
            }
        };
    }

    default <V> Injections<T> add(Key<V> key, Function<T, V> extractor) {
        return add(key, key.name(), extractor);
    }

    <V> Injections<T> add(Key<V> key, String fieldName, Function<T, V> extractor);

    <V> Function<T, PVector<V>> fromList(Function<T, V> extractor);
}
