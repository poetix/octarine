package com.codepoetics.octarine.records;

import org.pcollections.HashTreePSet;
import org.pcollections.PSet;

import java.util.Arrays;
import java.util.Collection;

public interface SetKey<T> extends Key<PSet<T>> {
    public static <T> SetKey<T> named(String name, Value... metadata) {
        return named(name, Record.of(metadata));
    }

    public static <T> SetKey<T> named(String name, Record metadata) {
        return new SetKey<T>() {
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

    default Value of(Collection<? extends T> values) { return of(HashTreePSet.from(values)); }
    default Value of(T... values) {
        return of(HashTreePSet.from(Arrays.asList(values)));
    }
}
