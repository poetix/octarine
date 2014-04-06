package com.codepoetics.octarine.records;

import com.codepoetics.octarine.morphisms.FluentCollection;
import com.codepoetics.octarine.morphisms.FluentMap;
import org.pcollections.PMap;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public interface Record {

    static Record empty() { return of(); }

    static Record of(Value...values) {
        return of(
            FluentMap.<Key<?>, Object>from(FluentCollection.from(values).toStream().map(Value::toPair)).toPMap()
        );
    }

    static Record of(PMap<Key<?>, Object> values) {
        if (values == null) { throw new IllegalArgumentException("values must not be null"); }
        return new Record() {

            @Override
            @SuppressWarnings("unchecked")
            public <T> Optional<T> get(Key<T> key) {
                return Optional.<T>ofNullable((T) values.get(key));
            }
            @Override
            public PMap<Key<?>, Object> values() {
                return values;
            }


            @Override
            public String toString() {
                return "{" + String.join(", ", values.entrySet()
                              .stream()
                              .map(e -> String.format("%s: %s", e.getKey().name(), e.getValue()))
                              .collect(Collectors.toList())) + "}";
            }

            @Override
            public int hashCode() { return values.hashCode(); }

            @Override
            public boolean equals(Object other) {
                if (other != null && other instanceof Record) {
                    return ((Record) other).values().equals(values);
                }
                return false;
            }
        };
    }

    <T> Optional<T> get(Key<T> key);
    PMap<Key<?>, Object> values();

    default Record with(Value...values) {
        return with(Record.of(values));
    }
    default Record with(PMap<Key<?>, Object> values) { return Record.of(values().plusAll(values)); }
    default Record with(Record other) { return with(other.values()); }

    default Record without(Key<?>...keys) {
        return without(FluentCollection.<Key<?>>from(keys).toSet());
    }
    default Record without(Set<Key<?>> keys) {
        return Record.of(values().minusAll(keys));
    }

}
