package com.codepoetics.octarine.records;

import org.pcollections.PVector;
import org.pcollections.TreePVector;

import java.util.Arrays;

public interface ListKey<T> extends Key<PVector<T>> {
    public static <T> ListKey<T> named(String name, Value...metadata) {
        return named(name, Record.of(metadata));
    }

    public static <T> ListKey<T> named(String name, Record metadata) {
        return new ListKey<T>() {
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

    default Value of(T...values) {
        return of(TreePVector.from(Arrays.asList(values)));
    }
}
