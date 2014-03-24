package com.codepoetics.octarine.records;

import com.codepoetics.octarine.lenses.Lens;
import com.codepoetics.octarine.lenses.OptionalLens;

import java.util.Optional;
import java.util.function.Supplier;

public interface Key<T> extends KeyLike<T>, OptionalLens<Record, T> {

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

            @Override
            public Optional<T> get(Record instance) {
                return from(instance);
            }

            @Override
            public Record set(Record instance, Optional<T> newValue) {
                if (newValue.isPresent()) {
                    return instance.with(this.of(newValue.get()));
                } else {
                    return instance.without(this);
                }
            }
        };
    }

    default Key<T> asKey() { return this; }

    default Optional<T> from(Record record) {
        return record.get(this);
    }

    default T from(Record record, T defaultValue) {
        return record.get(this).orElse(defaultValue);
    }

    default T from(Record record, Supplier<T> defaultValue) {
        return record.get(this).orElseGet(defaultValue);
    }

    public interface WithDefault<T> extends KeyLike<T>, Lens<Record, T> {
        default T from(Record record) {
            return get(record);
        }

        default String name() { return asKey().name(); }
        default Record metadata() { return asKey().metadata(); }
    }

    default WithDefault<T> withDefault(T defaultValue) {
        return withDefault(() -> defaultValue);
    }

    default WithDefault<T> withDefault(Supplier<T> defaultValue) {
        return new WithDefault<T>() {
            @Override
            public Key<T> asKey() { return Key.this; }

            @Override
            public T get(Record instance) {
                return instance.get(this).orElseGet(defaultValue);
            }

            @Override
            public Record set(Record instance, T newValue) {
                return instance.with(this.of(newValue));
            }
        };
    }
}
