package com.codepoetics.octarine.records;

import org.pcollections.PMap;

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class HashRecord implements Record {

    private final PMap<Key<?>, Object> values;

    HashRecord(PMap<Key<?>, Object> values) {
        this.values = values;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Optional<T> get(Key<T> key) {
        return Optional.ofNullable((T) values.get(key));
    }

    @Override
    public PMap<Key<?>, Object> values() {
        return values;
    }

    @Override
    public Record with(PMap<Key<?>, Object> values) {
        return Record.of(values().plusAll(values));
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
        if (other != null && other instanceof Record) {
            Record otherRecord = (Record) other;
            return otherRecord.values().equals(values);
        }
        return false;
    }
}
