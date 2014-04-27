package com.codepoetics.octarine.serialisation;

import com.codepoetics.octarine.records.Key;
import com.codepoetics.octarine.records.ListKey;
import org.pcollections.PVector;

import java.util.function.Function;

public interface InjectionCollector<T> {

    default <V> InjectionCollector<T> add(Key<V> key, Function<T, ? extends V> extractor) {
        return add(key, key.name(), extractor);
    }

    <V> InjectionCollector<T> add(Key<V> key, String fieldName, Function<T, ? extends V> extractor);

    default <V> InjectionCollector<T> addList(ListKey<V> key, String fieldName, Function<T, ? extends V> extractor) {
       return add(key, fieldName, fromList(extractor));
    }

    default <V> InjectionCollector<T> addList(ListKey<V> key, Function<T, ? extends V> extractor) {
       return addList(key, key.name(), extractor);
    }

    <V> Function<T, PVector<V>> fromList(Function<T, ? extends V> extractor);
}
