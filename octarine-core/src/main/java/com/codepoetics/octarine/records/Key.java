package com.codepoetics.octarine.records;

import com.codepoetics.octarine.lenses.OptionalLens;

import java.util.Optional;

public interface Key<T> extends OptionalLens<Record, T> {
    static <T> Key<T> named(String name) {
        return new Key<T>() {
           @Override public String name() { return name; }

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

    public String name();
    default Value of(T value) {
        return new Value() {
            @Override public Key<?> key() { return Key.this; }
            @Override public Object value() { return value; }
        };
    }

    default Optional<T> from(Record record) {
        return record.get(this);
    }

    default T from(Record record, T defaultValue) {
        return record.get(this, defaultValue);
    }

}
