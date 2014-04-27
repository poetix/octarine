package com.codepoetics.octarine.records;

import com.codepoetics.octarine.lenses.OptionalLens;
import com.codepoetics.octarine.functions.Extractor;
import com.codepoetics.octarine.paths.Path;

import java.util.Optional;
import java.util.function.Predicate;

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
    default public boolean test(Record instance) { return instance.containsKey(this); }

    @Override
    default public Optional<T> get(Record instance) {
        return instance.get(this);
    }

    @Override
    default public Record set(Record instance, Optional<T> newValue) {
        return newValue.isPresent()
            ? instance.with(this.of(newValue.get()))
            : instance.without(this);
    }

    String name();
    Record metadata();

    default Value of(T value) {
        return new Value() {
            @Override public Key<?> key() { return Key.this; }
            @Override public Object value() { return value; }
        };
    }

    default Extractor<Record, T> is(T expected) {
        return matches(Predicate.isEqual(expected));
    }

    default Extractor<Record, T> matches(Predicate<T> expected) {
        return Extractor.join(
                record -> record.get(Key.this).map(expected::test).orElse(false),
                this);
    }

}
