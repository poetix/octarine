package com.codepoetics.octarine.records;

import com.codepoetics.octarine.morphisms.FluentCollection;
import org.pcollections.HashTreePSet;
import org.pcollections.PMap;
import org.pcollections.PSet;

import java.util.Optional;
import java.util.Set;

public interface MutableRecord extends Record {

    default void set(Value...values) { set(Record.of(values)); }
    default void set(PMap<Key<?>, Object> values) { set(Record.of(values)); }
    void set(Record values);

    default void unset(Key<?>...keys) { unset(FluentCollection.<Key<?>>from(keys).toSet()); }
    void unset(Set<Key<?>> keys);

    Record added();
    Set<Key<?>> removed();

    public static MutableRecord from(Record record) {
        return new MutableRecord() {

            private Record current = record.with();
            private Record added = Record.empty();
            private PSet<Key<?>> removed = HashTreePSet.empty();
            @Override
            public void set(Record values) {
                added = added.with(values);
                removed = removed.minus(values.values().keySet());
                current = current.with(values);
            }

            @Override
            public void unset(Set<Key<?>> keys) {
                added = added.without(keys);
                current = current.without(keys);
                removed = removed.plusAll(keys);
            }

            @Override
            public Record added() {
                return added;
            }

            @Override
            public Set<Key<?>> removed() {
                return removed;
            }

            @Override
            public <T> Optional<T> get(Key<T> key) {
                return current.get(key);
            }

            @Override
            public PMap<Key<?>, Object> values() {
                return current.values();
            }

            @Override
            public Record with(PMap<Key<?>, Object> values) {
                return current.with(values);
            }

            @Override
            public Record immutable() {
                return current;
            }

            @Override
            public MutableRecord mutable() { return this; }

            @Override
            public String toString() {
                return "M" + current.toString();
            }

            @Override
            public int hashCode() { return current.hashCode(); }

            @Override
            public boolean equals(Object other) {
                return current.equals(other);
            }
        };
    }
}
