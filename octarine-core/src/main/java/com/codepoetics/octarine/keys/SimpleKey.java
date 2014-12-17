package com.codepoetics.octarine.keys;

import com.codepoetics.octarine.records.Record;

import java.util.Objects;

final class SimpleKey<T> implements Key<T> {

    private final String name;
    private final Record metadata;

    SimpleKey(String name, Record metadata) {
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
    public int hashCode() {
        return Objects.hash(name, metadata);
    }

    @Override
    public String toString() {
        return String.format("$%s %s", name, metadata);
    }
}
