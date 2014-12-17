package com.codepoetics.octarine.api;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface Record {

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
