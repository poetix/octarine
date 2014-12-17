package com.codepoetics.octarine.api;

import org.pcollections.PVector;
import org.pcollections.TreePVector;

import java.util.Arrays;
import java.util.Collection;

public interface ListKey<T> extends Key<PVector<T>> {

    default Value of(Collection<? extends T> values) {
        return of(TreePVector.from(values));
    }

    @SuppressWarnings("unchecked")
    default Value of(T... values) {
        return of(TreePVector.from(Arrays.asList(values)));
    }
}
