package com.codepoetics.octarine.keys;

import java.util.Objects;

public final class Value {

    private final Key<?> key;
    private final Object value;

    public Value(Key<?> key, Object value) {
        this.key = key;
        this.value = value;
    }

    public Key<?> key() {
        return key;
    }

    public Object value() {
        return value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, value);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final Value other = (Value) obj;
        return Objects.equals(this.key, other.key) && Objects.equals(this.value, other.value);
    }

    @Override
    public String toString() {
        return String.format("%s: %s", key.name(), value);
    }
}
