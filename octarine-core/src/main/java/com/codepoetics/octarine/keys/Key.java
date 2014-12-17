package com.codepoetics.octarine.keys;

import com.codepoetics.octarine.functional.lenses.OptionalLens;
import com.codepoetics.octarine.functional.paths.Path;
import com.codepoetics.octarine.records.Record;

import java.util.Optional;

public interface Key<T> extends OptionalLens<Record, T>, Path.Named<Record, T> {

    static <T> Key<T> named(String name, Value... metadata) {
        return named(name, Record.of(metadata));
    }

    static <T> Key<T> named(String name, Record metadata) {
        return new SimpleKey<T>(name, metadata);
    }

    @Override
    default public boolean test(Record instance) {
        return instance.containsKey(this);
    }

    @Override
    default public Optional<T> get(Record instance) {
        return instance.get(this);
    }

    @Override
    default public Record set(Record instance, Optional<T> newValue) {
        return newValue.map(value -> instance.with(of(value)))
                       .orElseGet(() -> instance.without(this));
    }

    String name();

    Record metadata();

    default Value of(T value) {
        return new Value(this, value);
    }

}
