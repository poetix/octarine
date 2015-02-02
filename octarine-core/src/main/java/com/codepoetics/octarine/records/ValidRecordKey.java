package com.codepoetics.octarine.records;

public interface ValidRecordKey<T> extends Key<Valid<T>> {

    static <T> ValidRecordKey<T> named(String name, Schema<T> schema, Value...metadata) {
        return named(name, schema, Record.of(metadata));
    }

    static <T> ValidRecordKey<T> named(String name, Schema<T> schema, Record metadata) {
        return new Impl<T>(name, schema, metadata);
    }

    static final class Impl<T> extends BaseKey<Valid<T>> implements ValidRecordKey<T> {

        private final Schema<T> schema;

        Impl(String name, Schema<T> schema, Record metadata) {
            super(name, metadata);
            this.schema = schema;
        }

        @Override
        public Schema<T> schema() {
            return schema;
        }

        @Override
        public Value of(Value...values) {
            return of(schema.validate(values).get());
        }
    }

    Schema<T> schema();

    Value of(Value... values);
}
