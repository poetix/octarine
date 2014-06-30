package com.codepoetics.octarine.records;

public interface RecordKey extends Key<Record> {

    public static RecordKey named(String name, Value... metadata) {
        return named(name, Record.of(metadata));
    }

    public static RecordKey named(String name, Record metadata) {
        return new RecordKey() {
            @Override
            public String name() {
                return name;
            }

            @Override
            public Record metadata() {
                return metadata;
            }
        };
    }

    default Value of(Value... values) {
        return of(Record.of(values));
    }
}
