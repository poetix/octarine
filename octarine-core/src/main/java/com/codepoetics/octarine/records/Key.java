package com.codepoetics.octarine.records;

import com.codepoetics.octarine.lenses.OptionalLens;
import com.codepoetics.octarine.paths.Path;

import java.util.Optional;
import java.util.function.Supplier;

public interface Key<T> extends OptionalLens<Record, T>, Path.Named<Record, T> {

    static <T> Key<T> named(String name, Value...metadata) {
        return named(name, Record.of(metadata));
    }

    static <T> Key<T> named(String name, Record metadata) {
        return new Key<T>() {
            @Override
            public String name() {
                return name;
            }

            @Override
            public Record metadata() { return metadata; }
        };
    }

    @Override
    default public Optional<T> get(Record instance) {
        return from(instance);
    }

    @Override
    default public Record set(Record instance, Optional<T> newValue) {
        return newValue.isPresent()
            ? instance.with(this.of(newValue.get()))
            : instance.without(this);
    }

    default Optional<T> from(Record record) {
        return record.get(this);
    }

    default T from(Record record, T defaultValue) {
        return record.get(this).orElse(defaultValue);
    }

    default T from(Record record, Supplier<T> defaultValue) {
        return record.get(this).orElseGet(defaultValue);
    }

    String name();
    Record metadata();

    default Value of(T value) {
        return new Value() {
            @Override public Key<?> key() { return Key.this; }
            @Override public Object value() { return value; }
        };
    }

}
