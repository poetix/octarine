package com.codepoetics.octarine;

import com.codepoetics.octarine.api.*;
import com.codepoetics.octarine.keys.Keys;
import com.codepoetics.octarine.records.HashRecord;
import com.codepoetics.octarine.validation.api.Schema;
import com.codepoetics.octarine.validation.api.ValidRecordKey;

import java.util.stream.Stream;

public final class Octarine {
    private Octarine() { }

    public static <T> Key<T> $(String name, Value...metadata) {
        return Keys.simpleKey(name, $$(metadata));
    }

    public static <T> Key<T> $(String name, Record metadata) {
        return Keys.simpleKey(name, metadata);
    }

    public static <T> ListKey<T> $L(String name, Value...metadata) {
        return $L(name, $$(metadata));
    }

    public static <T> ListKey<T> $L(String name, Record metadata) {
        return Keys.listKey(name, metadata);
    }

    public static RecordKey $R(String name, Value...metadata) {
        return $R(name, $$(metadata));
    }

    public static RecordKey $R(String name, Record metadata) {
        return Keys.recordKey(name, metadata);
    }

    public static <T> MapKey<T> $M(String name, Value...metadata) {
        return $M(name, $$(metadata));
    }

    public static <T> MapKey<T> $M(String name, Record metadata) {
        return Keys.mapKey(name, metadata);
    }

    public static <T>ValidRecordKey<T> $V(String name, Schema<T> schema, Value...metadata) {
        return $V(name, schema, $$(metadata));
    }

    public static <T>ValidRecordKey<T> $V(String name, Schema<T> schema, Record metadata) {
        return Keys.validRecordKey(name, schema, metadata);
    }

    public static Record $$(Value...values) { return HashRecord.of(values); }
    public static Record $$(Record source, Value...values) { return source.with(values); }
    public static Record $$(Record...records) {
        return Stream.of(records).reduce(HashRecord.empty(), Record::with);
    }

}
