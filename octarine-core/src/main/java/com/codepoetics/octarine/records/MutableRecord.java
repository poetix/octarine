package com.codepoetics.octarine.records;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface MutableRecord extends Record {

    void set(Value... values);

    void set(Record values);

    default void unset(Key<?>... keys) {
        unset(Stream.of(keys).collect(Collectors.toSet()));
    }

    void unset(Collection<Key<?>> keys);

    Record added();

    Set<Key<?>> removed();

}
