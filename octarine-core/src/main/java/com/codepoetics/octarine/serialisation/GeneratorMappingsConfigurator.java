package com.codepoetics.octarine.serialisation;

import com.codepoetics.octarine.records.Key;

import java.util.function.BiConsumer;

public interface GeneratorMappingsConfigurator<T> {
    <V> GeneratorMappingsConfigurator<T> add(Key<? extends V> key, String fieldName, BiConsumer<T, ? extends V> serialiser);
    default <V> GeneratorMappingsConfigurator<T> add(Key<? extends V> key, BiConsumer<T, ? extends V> serialiser) {
        return add(key, key.name(), serialiser);
    }
}
