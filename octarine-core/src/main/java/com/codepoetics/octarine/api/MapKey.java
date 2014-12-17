package com.codepoetics.octarine.api;

import org.pcollections.HashTreePMap;
import org.pcollections.PMap;

import java.util.Map;

public interface MapKey<T> extends Key<PMap<String, T>> {

    default Value of(Map<String, ? extends T> values) {
        return of(HashTreePMap.from(values));
    }

}
