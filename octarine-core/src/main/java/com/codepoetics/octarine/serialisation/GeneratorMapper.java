package com.codepoetics.octarine.serialisation;

import com.codepoetics.octarine.records.Key;

import java.util.function.Consumer;
import java.util.function.Function;

public interface GeneratorMapper<T> {
    void generateValues(T generator, Consumer<String> fieldNameConsumer, Function<Key<?>, Object> valueGetter);
}
