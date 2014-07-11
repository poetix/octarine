package com.codepoetics.octarine.records;

import org.pcollections.HashTreePMap;
import org.pcollections.PMap;

import java.util.Map;

public interface MapKey<T> extends Key<PMap<String, T>> {
    public static <T> MapKey<T> named(String name, Value... metadata) {
        return named(name, Record.of(metadata));
    }

    public static <T> MapKey<T> named(String name, Record metadata) {
        return new MapKey<T>() {
            @Override
            public String name() {
                return name;
            }

            @Override
            public Record metadata() {
                return metadata;
            }
        };
    }

    default Value of(Map<String, ? extends T> values) {
        return of(HashTreePMap.from(values));
    }
}
