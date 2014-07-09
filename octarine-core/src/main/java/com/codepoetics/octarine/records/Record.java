package com.codepoetics.octarine.records;

import org.pcollections.HashTreePMap;
import org.pcollections.PMap;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface Record {

    static Record empty() {
        return of();
    }

    static Record of(Collection<Value> values) {
        return of(values.stream());
    }

    static Record of(Value...values) {
        return of(Stream.of(values));
    }

    static Record of(Stream<Value> values) {
        return of(HashTreePMap.from(values.collect(Collectors.toMap(Value::key, Value::value))));
    }

    static Record of(PMap<Key<?>, Object> values) {
        return new HashRecord(values);
    }

    <T> Optional<T> get(Key<T> key);

    PMap<Key<?>, Object> values();

    Record with(PMap<Key<?>, Object> values);

    default boolean containsKey(Key<?> key) {
        return values().containsKey(key);
    }

    default Record with(Value... values) {
        return with(Record.of(values));
    }

    default Record with(Record other) {
        return with(other.values());
    }

    default Record without(Key<?>... keys) {
        return without(Stream.of(keys).collect(Collectors.toSet()));
    }

    default Record without(Set<Key<?>> keys) {
        return Record.of(values().minusAll(keys));
    }

    default MutableRecord mutable() {
        return MutableRecord.from(this);
    }

    default Record immutable() {
        return this;
    }

    default Record select(Key<?>... keys) {
        Map<Key<?>, Object> selected =
                Arrays.stream(keys)
                        .filter(this::containsKey)
                        .collect(Collectors.toMap(
                                Function.identity(),
                                k -> k.extract(this)));
        return Record.of(HashTreePMap.from(selected));
    }

}
