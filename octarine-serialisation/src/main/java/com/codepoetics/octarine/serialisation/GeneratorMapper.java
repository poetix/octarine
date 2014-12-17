package com.codepoetics.octarine.serialisation;

import com.codepoetics.octarine.api.Key;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

public interface GeneratorMapper<T> {
    <V> void generateValues(T generator, Consumer<String> fieldNameConsumer, Function<Key<V>, Optional<V>> valueGetter);
}
