package com.codepoetics.octarine.records;

import org.pcollections.HashTreePMap;
import org.pcollections.PMap;

import java.util.HashMap;
import java.util.Map;

public interface MapKey<T> extends Key<PMap<String, T>> {

    static <T> MapKey<T> named(String name, Value...metadata) {
        return named(name, Record.of(metadata));
    }

    static <T> MapKey<T> named(String name, Record metadata) {
        return new Impl<T>(name, metadata);
    }

    static final class Impl<T> extends BaseKey<PMap<String, T>> implements MapKey<T> {
        Impl(String name, Record metadata) {
            super(name, metadata);
        }
    }

    default Value of(Map<String, ? extends T> values) {
        return of(HashTreePMap.from(values));
    }
    default Value of(String key1, T value1) {
        Map<String, T> target = new HashMap<>();
        target.put(key1, value1);

        return of(target);
    }

    default Value of(String key1, T value1, String key2, T value2) {
        Map<String, T> target = new HashMap<>();
        target.put(key1, value1);
        target.put(key2, value2);

        return of(target);
    }

    default Value of(String key1, T value1, String key2, T value2, String key3, T value3) {
        Map<String, T> target = new HashMap<>();
        target.put(key1, value1);
        target.put(key2, value2);
        target.put(key3, value3);

        return of(target);
    }

    default Value of(String key1, T value1, String key2, T value2, String key3, T value3,
                     String key4, T value4) {
        Map<String, T> target = new HashMap<>();
        target.put(key1, value1);
        target.put(key2, value2);
        target.put(key3, value3);
        target.put(key4, value4);

        return of(target);
    }

    default Value of(String key1, T value1, String key2, T value2, String key3, T value3,
                     String key4, T value4, String key5, T value5) {
        Map<String, T> target = new HashMap<>();
        target.put(key1, value1);
        target.put(key2, value2);
        target.put(key3, value3);
        target.put(key4, value4);
        target.put(key5, value5);

        return of(target);
    }

    default Value of(String key1, T value1, String key2, T value2, String key3, T value3,
                     String key4, T value4, String key5, T value5, String key6, T value6) {
        Map<String, T> target = new HashMap<>();
        target.put(key1, value1);
        target.put(key2, value2);
        target.put(key3, value3);
        target.put(key4, value4);
        target.put(key5, value5);
        target.put(key6, value6);

        return of(target);
    }
}
