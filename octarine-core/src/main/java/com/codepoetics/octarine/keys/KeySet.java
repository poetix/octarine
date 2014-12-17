package com.codepoetics.octarine.keys;

import com.codepoetics.octarine.api.*;
import com.codepoetics.octarine.records.HashRecord;
import com.codepoetics.octarine.validation.api.Schema;
import com.codepoetics.octarine.validation.api.ValidRecordKey;

import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class KeySet implements BiConsumer<Record, Consumer<String>> {

    private final Set<Key<?>> keys = new HashSet<>();

    public <T> Key<T> add(String name, Value... metadata) {
        return add(name, HashRecord.of(metadata));
    }

    public <T> Key<T> add(String name, Record metadata) {
        return add(Keys.simpleKey(name, metadata));
    }

    public <T> ListKey<T> addList(String name, Value... metadata) {
        return addList(name, HashRecord.of(metadata));
    }

    public <T> ListKey<T> addList(String name, Record metadata) {
        return add(Keys.listKey(name, metadata));
    }

    public RecordKey addRecord(String name, Value... metadata) {
        return addRecord(name, HashRecord.of(metadata));
    }

    public RecordKey addRecord(String name, Record metadata) {
        return add(Keys.recordKey(name, metadata));
    }

    public <T> ValidRecordKey<T> addValidRecord(String name, Schema<T> schema, Value... metadata) {
        return addValidRecord(name, schema, HashRecord.of(metadata));
    }

    public <T> ValidRecordKey<T> addValidRecord(String name, Schema<T> schema, Record metadata) {
        return add(Keys.validRecordKey(name, schema, metadata));
    }

    public <T, K extends Key<T>> K add(K key) {
        keys.add(key);
        return key;
    }

    @Override
    public void accept(Record record, Consumer<String> validationErrors) {
        Set<Key<?>> keysInRecord = record.values().keySet();
        keys.stream().filter(key -> !keysInRecord.contains(key)).forEach(key -> {
            validationErrors.accept(String.format("Missing key \"%s\"", key.name()));
        });
    }
}
