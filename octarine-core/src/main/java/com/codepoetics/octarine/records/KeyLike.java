package com.codepoetics.octarine.records;

public interface KeyLike<T> {
    String name();
    Record metadata();

    Key<T> asKey();

    default Value of(T value) {
        return new Value() {
            @Override public Key<?> key() { return asKey(); }
            @Override public Object value() { return value; }
        };
    }
}
