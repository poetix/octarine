package com.codepoetics.octarine.api;

import com.codepoetics.octarine.functional.lenses.OptionalLens;
import com.codepoetics.octarine.functional.paths.Path;

import java.util.Optional;

public interface Key<T> extends OptionalLens<Record, T>, Path.Named<Record, T> {

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

    Value of(T value);

}
