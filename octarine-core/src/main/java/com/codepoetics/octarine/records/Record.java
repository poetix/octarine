package com.codepoetics.octarine.records;

import com.codepoetics.octarine.morphisms.FluentCollection;
import com.codepoetics.octarine.morphisms.FluentMap;
import com.codepoetics.octarine.records.fixed.FixedRecord;
import org.pcollections.PMap;

import java.util.List;
import java.util.Optional;
import java.util.Set;
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
                return Record.toString(this);
            }

            @Override
            public int hashCode() { return Record.hashCode(this); }

            @Override

            public boolean equals(Object other) {
                return Record.equals(this, other);
            }
        };
    }

    <T> Optional<T> get(Key<T> key);
    PMap<Key<?>, Object> values();

    default boolean containsKey(Key<?> key) {
        return values().containsKey(key);
    }

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

    default MutableRecord mutable() { return MutableRecord.from(this); }
    default Record immutable() { return this; }
    default Record fixed() { return FixedRecord.of(values()); }

    static String toString(Record record) {
        Stream<String> descriptions = record.values().entrySet()
                .stream()
                .map(e -> String.format("%s: %s", e.getKey().name(), e.getValue()));
        return "{" + String.join(", ", descriptions.collect(Collectors.toList())) + "}";
    }

    static int hashCode(Record record) {
        return record.values().hashCode();
    }

    static boolean equals(Record self, Object other) {
        if (other != null && other instanceof Record) {
            return ((Record) other).values().equals(self.values());
        }
        return false;
    }

}
