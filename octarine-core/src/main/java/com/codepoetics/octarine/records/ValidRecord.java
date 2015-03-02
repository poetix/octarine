package com.codepoetics.octarine.records;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

final class ValidRecord<T> implements Valid<T> {
    private final Record record;
    private final Schema schema;

    public ValidRecord(Record record, Schema schema) {
        this.record = record;
        this.schema = schema;
    }

    @Override
    public <S> Optional<S> get(Key<? extends S> key) {
        return record.get(key);
    }

    @Override
    public Set<Key<?>> keys() {
        return record.keys();
    }

    @Override
    public Map<Key<?>, Object> values() {
        return record.values();
    }

    @Override
    public boolean containsKey(Key<?> key) {
        return record.containsKey(key);
    }

    @Override
    public Record with(Value... values) {
        return record.with(values);
    }

    @Override
    public Record with(Record other) {
        return record.with(other);
    }

    @Override
    public Record without(Collection<Key<?>> keys) {
        return record.without(keys);
    }

    @Override
    public MutableRecord mutable() {
        return record.mutable();
    }

    @Override
    public Record select(Collection<Key<?>> selectedKeys) {
        return record.select(selectedKeys);
    }

    @Override
    public String toString() {
        return record.toString();
    }

    @Override
    public int hashCode() {
        return record.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        return record.equals(other);
    }

    @Override
    public Schema<T> schema() {
        return schema;
    }
}
