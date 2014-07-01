package com.codepoetics.octarine.records;

import org.pcollections.HashTreePSet;
import org.pcollections.PMap;
import org.pcollections.PSet;

import java.util.Optional;
import java.util.Set;

class WrappingMutableRecord implements MutableRecord {

    public static MutableRecord wrap(Record record) {
        return new WrappingMutableRecord(record.with());
    }

    private Record current;
    private Record added;
    private PSet<Key<?>> removed;

    private WrappingMutableRecord(Record current) {
        this.current = current;
        added = Record.empty();
        removed = HashTreePSet.empty();
    }

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
    public MutableRecord mutable() {
        return this;
    }

    @Override
    public String toString() {
        return current.toString();
    }

    @Override
    public int hashCode() {
        return current.hashCode();
    }

    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    @Override
    public boolean equals(Object other) {
        return current.equals(other);
    }
}
