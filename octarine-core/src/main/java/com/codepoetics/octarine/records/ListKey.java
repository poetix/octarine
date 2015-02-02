package com.codepoetics.octarine.records;

import org.pcollections.PVector;
import org.pcollections.TreePVector;

import java.util.Arrays;
import java.util.Collection;

public interface ListKey<T> extends Key<PVector<T>> {

    static <T> ListKey<T> named(String name, Value...metadata) {
        return named(name, Record.of(metadata));
    }

    static <T> ListKey<T> named(String name, Record metadata) {
        return new Impl<T>(name, metadata);
    }

    static final class Impl<T> extends BaseKey<PVector<T>> implements ListKey<T> {
        Impl(String name, Record metadata) {
            super(name, metadata);
        }
    }

    default Value of(Collection<? extends T> values) {
        return of(TreePVector.from(values));
    }

    @SuppressWarnings("unchecked")
    default Value of(T... values) {
        return of(Arrays.asList(values));
    }
}
