package com.codepoetics.octarine.records;

import org.pcollections.HashTreePMap;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface Record {

    static final Record EMPTY_RECORD = new HashRecord(HashTreePMap.empty());

    public static Record empty() {
        return EMPTY_RECORD;
    }

    public static Record of(Collection<Value> values) {
        return of(values.stream());
    }

    public static Record of(Value...values) {
        return values.length == 0 ? empty() : of(Stream.of(values));
    }

    public static Record of(Stream<Value> values) {
        return new HashRecord(HashTreePMap.from(values.collect(Collectors.toMap(Value::key, Value::value))));
    }

    <T> Optional<T> get(Key<T> key);

    Set<Key<?>> keys();

    Map<Key<?>, Object> values();

    boolean containsKey(Key<?> key);

    Record with(Value... values);

    Record with(Record other);

    default Record without(Key<?>... keys) {
        return without(Stream.of(keys).collect(Collectors.toSet()));
    }

    Record without(Collection<Key<?>> keys);

    MutableRecord mutable();

    default Record immutable() {
        return this;
    }

    Record select(Collection<Key<?>> selectedKeys);

    default Record select(Key<?>... keys) {
        return select(Stream.of(keys).collect(Collectors.toSet()));
    }

}
