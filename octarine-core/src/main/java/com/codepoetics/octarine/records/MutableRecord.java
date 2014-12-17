package com.codepoetics.octarine.records;

import com.codepoetics.octarine.keys.Key;
import com.codepoetics.octarine.keys.Value;
import org.pcollections.PMap;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface MutableRecord extends Record {

    public static MutableRecord from(Record record) {
        return WrappingMutableRecord.wrap(record);
    }

    default void set(Value... values) {
        set(Record.of(values));
    }

    default void set(PMap<Key<?>, Object> values) {
        set(Record.of(values));
    }

    void set(Record values);

    default void unset(Key<?>... keys) {
        unset(Stream.of(keys).collect(Collectors.toSet()));
    }

    void unset(Set<Key<?>> keys);

    Record added();

    Set<Key<?>> removed();

}
