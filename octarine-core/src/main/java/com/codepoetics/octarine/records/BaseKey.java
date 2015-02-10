package com.codepoetics.octarine.records;

import java.util.Objects;

abstract class BaseKey<T> implements Key<T> {
    private final String name;
    private final Record metadata;

    BaseKey(String name, Record metadata) {
        this.name = name;
        this.metadata = metadata;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public Record metadata() {
        return metadata;
    }

    @Override
    public Value of(T value) {
        return new ConcreteValue(this, value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, metadata);
    }

    @Override
    public void describe(StringBuilder sb) {
        if (sb.length() > 0) {
            sb.append(".");
        }

        sb.append(name);
    }

    @Override
    public String toString() {
        return String.format("$%s %s", name, metadata);
    }
}
