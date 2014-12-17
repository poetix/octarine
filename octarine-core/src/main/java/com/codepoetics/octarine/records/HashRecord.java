package com.codepoetics.octarine.records;

import com.codepoetics.octarine.api.Key;
import com.codepoetics.octarine.api.MutableRecord;
import com.codepoetics.octarine.api.Record;
import com.codepoetics.octarine.api.Value;
import org.pcollections.HashTreePMap;
import org.pcollections.PMap;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class HashRecord implements Record {

    private static final Record EMPTY_RECORD = new HashRecord(HashTreePMap.empty());

    public static Record empty() {
        return EMPTY_RECORD;
    }

    public static Record of(Collection<Value> values) {
        return of(values.stream());
    }

    public static Record of(Value...values) {
        return of(Stream.of(values));
    }

    public static Record of(Stream<Value> values) {
        return new HashRecord(HashTreePMap.from(values.collect(Collectors.toMap(Value::key, Value::value))));
    }

    private final PMap<Key<?>, Object> values;

    private HashRecord(PMap<Key<?>, Object> values) {
        this.values = values;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Optional<T> get(Key<T> key) {
        return Optional.ofNullable((T) values.get(key));
    }

    @Override
    public Set<Key<?>> keys() {
        return values.keySet();
    }

    @Override
    public PMap<Key<?>, Object> values() {
        return values;
    }

    private Record with(Map<Key<?>, Object> values) {
        return new HashRecord(this.values.plusAll(values));
    }

    @Override
    public boolean containsKey(Key<?> key) {
        return values.containsKey(key);
    }

    @Override
    public Record with(Value... values) {
        return with(Stream.of(values).collect(Collectors.toMap(Value::key, Value::value)));
    }

    @Override
    public Record with(Record other) {
        return with(other.values());
    }

    @Override
    public Record without(Collection<Key<?>> keys) {
        return new HashRecord(this.values.minusAll(keys));
    }

    @Override
    public MutableRecord mutable() {
        return WrappingMutableRecord.wrap(this);
    }

    @Override
    public Record select(Collection<Key<?>> selectedKeys) {
        Set<Key<?>> keysToDrop = new HashSet<>(keys());
        keysToDrop.removeAll(selectedKeys);
        return without(keysToDrop);
    }

    @Override
    public String toString() {
        Stream<String> descriptions = values.entrySet()
                .stream()
                .map(e -> String.format("%s: %s", e.getKey().name(), e.getValue()));
        return "{" + String.join(", ", descriptions.collect(Collectors.toList())) + "}";
    }

    @Override
    public int hashCode() {
        return values.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Record) {
            return ((Record) other).values().equals(values);
        }
        return false;
    }
}
