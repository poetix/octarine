package com.codepoetics.octarine.validation;

import com.codepoetics.octarine.keys.Key;
import com.codepoetics.octarine.records.Record;
import com.codepoetics.octarine.keys.Value;

public interface ValidRecordKey<T> extends Key<Valid<T>> {


    public static <T> ValidRecordKey<T> named(String name, Schema<T> schema, Value... metadata) {
        return named(name, schema, Record.of(metadata));
    }

    public static <T> ValidRecordKey<T> named(String name, Schema<T> schema, Record metadata) {
        return new ValidRecordKey<T>() {
            @Override
            public Schema<T> schema() {
                return schema;
            }

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

    Schema<T> schema();

    default Value of(Value... values) {
        return of(schema().validate(values).get());
    }
}
