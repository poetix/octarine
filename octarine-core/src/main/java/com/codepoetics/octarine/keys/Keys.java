package com.codepoetics.octarine.keys;

import com.codepoetics.octarine.api.*;
import com.codepoetics.octarine.records.HashRecord;
import com.codepoetics.octarine.validation.api.Schema;
import com.codepoetics.octarine.validation.api.Valid;
import com.codepoetics.octarine.validation.api.ValidRecordKey;
import org.pcollections.PMap;
import org.pcollections.PVector;

import java.util.Objects;

public final class Keys<T>  {

    private Keys() {
    }

    public static <T> Key<T> simpleKey(String name) {
        return new SimpleKey<>(name, HashRecord.empty());
    }

    public static <T> Key<T> simpleKey(String name, Record metadata) {
        return new SimpleKey<>(name, metadata);
    }

    public static <T> ListKey<T> listKey(String name, Record metadata) {
        return new ConcreteListKey<>(name, metadata);
    }

    public static <T> MapKey<T> mapKey(String name, Record metadata) {
        return new ConcreteMapKey<>(name, metadata);
    }

    public static RecordKey recordKey(String name, Record metadata) {
        return new ConcreteRecordKey<>(name, metadata);
    }

    public static <T> ValidRecordKey<T> validRecordKey(String name, Schema<T> schema, Record metadata) {
        return new ConcreteValidRecordKey<>(name, schema, metadata);
    }

    private static abstract class BaseKey<T> implements Key<T> {
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
        public String toString() {
            return String.format("$%s %s", name, metadata);
        }
    }

    private static final class SimpleKey<T> extends BaseKey<T> {

        SimpleKey(String name, Record metadata) {
            super(name, metadata);
        }

    }

    private static final class ConcreteListKey<T> extends BaseKey<PVector<T>> implements ListKey<T> {

        ConcreteListKey(String name, Record metadata) {
            super(name, metadata);
        }

    }

    private static final class ConcreteMapKey<T> extends BaseKey<PMap<String, T>> implements MapKey<T> {

        ConcreteMapKey(String name, Record metadata) {
            super(name, metadata);
        }

    }

    private static final class ConcreteRecordKey<T> extends BaseKey<Record> implements RecordKey {

        ConcreteRecordKey(String name, Record metadata) {
            super(name, metadata);
        }

        @Override
        public Value of(Value... values) {
            return of(HashRecord.of(values));
        }
    }

    private static final class ConcreteValidRecordKey<T> extends BaseKey<Valid<T>> implements ValidRecordKey<T> {

        private final Schema<T> schema;

        private ConcreteValidRecordKey(String name, Schema<T> schema, Record metadata) {
            super(name, metadata);
            this.schema = schema;
        }

        @Override
        public Schema<T> schema() {
            return schema;
        }

    }

}
