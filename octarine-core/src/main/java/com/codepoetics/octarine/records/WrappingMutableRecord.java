package com.codepoetics.octarine.records;

import com.codepoetics.octarine.api.Key;
import com.codepoetics.octarine.api.MutableRecord;
import com.codepoetics.octarine.api.Record;
import com.codepoetics.octarine.api.Value;

import java.util.*;

final class WrappingMutableRecord implements MutableRecord {

    public static MutableRecord wrap(Record record) {
        return new WrappingMutableRecord(record.with());
    }

    private Record current;
    private Record added;
    private final Set<Key<?>> removed = new HashSet<>();

    private WrappingMutableRecord(Record current) {
        this.current = current;
        added = HashRecord.empty();
    }

    @Override
    public void set(Value... values) {
        set(HashRecord.of(values));
    }

    @Override
    public void set(Record values) {
        added = added.with(values);
        removed.removeAll(values.keys());
        current = current.with(values);
    }

    @Override
    public void unset(Collection<Key<?>> keys) {
        added = added.without(keys);
        current = current.without(keys);
        removed.addAll(keys);
    }

    @Override
    public Record added() {
        return added;
    }

    @Override
    public Set<Key<?>> removed() {
        return new HashSet<>(removed);
    }

    @Override
    public <T> Optional<T> get(Key<T> key) {
        return current.get(key);
    }

    @Override
    public Set<Key<?>> keys() {
        return current.keys();
    }

    @Override
    public Map<Key<?>, Object> values() {
        return current.values();
    }

    @Override
    public boolean containsKey(Key<?> key) {
        return current.containsKey(key);
    }

    @Override
    public Record with(Value... values) {
        return current.with(values);
    }

    @Override
    public Record with(Record other) {
        return current.with(other);
    }

    @Override
    public Record without(Collection<Key<?>> keys) {
        return current.without(keys);
    }

    @Override
    public Record immutable() {
        return current;
    }

    @Override
    public Record select(Collection<Key<?>> selectedKeys) {
        return current.select(selectedKeys);
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
