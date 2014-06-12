package com.codepoetics.octarine.records;

import com.codepoetics.octarine.morphisms.FluentCollection;
import com.codepoetics.octarine.morphisms.FluentMap;
import org.pcollections.HashTreePMap;
import org.pcollections.PMap;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface Record {

    static Record empty() { return of(); }
    static Record of(List<Value> values) {
        return of(FluentCollection.from(values).toStream());
    }

    static Record of(Value...values) {
        return of(FluentCollection.from(values).toStream());
    }

    static Record of(Stream<Value> values) {
        return of(FluentMap.<Key<?>, Object>from(values.map(Value::toPair)).toPMap());
    }

    static Record of(PMap<Key<?>, Object> values) {
        if (values == null) { throw new IllegalArgumentException("values must not be null"); }
        return new HashRecord(values);
    }

    <T> Optional<T> get(Key<T> key);
    PMap<Key<?>, Object> values();
    Record with(PMap<Key<?>, Object> values);

    default boolean containsKey(Key<?> key) {
        return values().containsKey(key);
    }

    default Record with(Value...values) {
        return with(Record.of(values));
    }

    default Record with(Record other) { return with(other.values()); }

    default Record without(Key<?>...keys) {
        return without(FluentCollection.<Key<?>>from(keys).toSet());
    }
    default Record without(Set<Key<?>> keys) {
        return Record.of(values().minusAll(keys));
    }

    default MutableRecord mutable() { return MutableRecord.from(this); }
    default Record immutable() { return this; }

    default Record select(Key<?>...keys) {
        Map<Key<?>, Object> selected =
            Arrays.stream(keys)
                  .filter(this::containsKey)
                  .collect(Collectors.toMap(
                      Function.identity(),
                      k -> k.extract(this)));
        return Record.of(HashTreePMap.from(selected));
    }

}
