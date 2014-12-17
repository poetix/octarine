package com.codepoetics.octarine.keys;

import com.codepoetics.octarine.records.Record;
import org.pcollections.HashTreePMap;
import org.pcollections.PMap;

import java.util.Map;
import java.util.Objects;

public final class MapKey<T> implements Key<PMap<String, T>> {
    public static <T> MapKey<T> named(String name, Value... metadata) {
        return named(name, Record.of(metadata));
    }

    public static <T> MapKey<T> named(String name, Record metadata) {
        return new MapKey<>(name, metadata);
    }

    private final String name;
    private final Record metadata;

    private MapKey(String name, Record metadata) {
        this.name = name;
        this.metadata = metadata;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public Record metadata() {
        return metadata;
    }


    public Value of(Map<String, ? extends T> values) {
        return of(HashTreePMap.from(values));
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, metadata);
    }

    @Override
    public String toString() {
        return String.format("$%s (map) %s", name, metadata);
    }
}
