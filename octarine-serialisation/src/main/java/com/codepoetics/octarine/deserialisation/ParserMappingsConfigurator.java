package com.codepoetics.octarine.deserialisation;

import com.codepoetics.octarine.keys.Key;

import java.util.function.Function;

public interface ParserMappingsConfigurator<T> {
    <V> ParserMappingsConfigurator<T> add(Key<? extends V> key, String fieldName, Function<T, ? extends V> deserialiser);

    default <V> ParserMappingsConfigurator<T> add(Key<? extends V> key, Function<T, ? extends V> deserialiser) {
        return add(key, key.name(), deserialiser);
    }
}
