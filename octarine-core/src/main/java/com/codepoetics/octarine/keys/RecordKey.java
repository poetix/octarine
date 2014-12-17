package com.codepoetics.octarine.keys;

import com.codepoetics.octarine.records.Record;

import java.util.Objects;

public final class RecordKey implements Key<Record> {

    public static RecordKey named(String name, Value... metadata) {
        return named(name, Record.of(metadata));
    }

    public static RecordKey named(String name, Record metadata) {
        return new RecordKey(name, metadata);
    }

    private final String name;
    private final Record metadata;

    private RecordKey(String name, Record metadata) {
        this.name = name;
        this.metadata = metadata;
    }

    public Value of(Value... values) {
        return of(Record.of(values));
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
        return String.format("$%s (record) %s", name, metadata);
    }
}
