package com.codepoetics.octarine.records;

public interface RecordKey extends Key<Record> {
    static <T> RecordKey named(String name, Value...metadata) {
        return named(name, Record.of(metadata));
    }

    static <T> RecordKey named(String name, Record metadata) {
        return new Impl<T>(name, metadata);
    }

    static final class Impl<T> extends BaseKey<Record> implements RecordKey {
        Impl(String name, Record metadata) {
            super(name, metadata);
        }
    }

    default Value of(Value... values) {
        return of(Record.of(values));
    }

}
