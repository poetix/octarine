package com.codepoetics.octarine.records;

import com.codepoetics.octarine.functional.lenses.OptionalLens;
import com.codepoetics.octarine.functional.paths.Path;

import java.util.Optional;

public interface Key<T> extends OptionalLens<Record, T>, Path.Named<Record, T> {

    static <T> Key<T> named(String name, Value...metadata) {
        return named(name, Record.of(metadata));
    }

    static <T> Key<T> named(String name, Record metadata) {
        return new Impl<>(name, metadata);
    }

    static final class Impl<T> extends BaseKey<T> {
        Impl(String name, Record metadata) {
            super(name, metadata);
        }
    }

    @Override
    default public boolean test(Record instance) {
        return instance.containsKey(this);
    }

    @Override
    default public Optional<T> get(Record record) {
        return record.get(this);
    }

    @Override
    default public Record set(Record record, Optional<T> newValue) {
        return newValue.map(value -> record.with(of(value))).orElseGet(() -> record.without(this));
    }

    String name();

    Record metadata();

    Value of(T value);

}
