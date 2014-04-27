package com.codepoetics.octarine.serialisation;

import com.codepoetics.octarine.records.Key;
import com.codepoetics.octarine.records.Record;
import org.pcollections.HashTreePMap;
import org.pcollections.PVector;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public interface Injections<T> extends InjectionCollector<T>, Supplier<RecordBuilder<T>> {

    static <T> Injections<T> against(Deserialiser<T> deserialiser) {
        return new Injections<T>() {

            private final Map<String, Consumer<T>> fieldDeserialisers = new HashMap<>();
            private final Map<Key<?>, Object> values = new HashMap<>();

            @Override
            public <V> InjectionCollector<T> add(Key<V> key, String fieldName, Function<T, ? extends V> extractor) {
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
            public <V> Function<T, PVector<V>> fromList(Function<T, ? extends V> extractor) {
                return reader -> deserialiser.readList(reader, extractor);
            }
        };
    }

}
