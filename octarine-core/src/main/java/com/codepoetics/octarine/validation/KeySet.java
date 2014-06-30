package com.codepoetics.octarine.validation;

import com.codepoetics.octarine.records.*;

import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class KeySet implements BiConsumer<Record, Consumer<String>> {

    private final Set<Key<?>> keys = new HashSet<>();

    public <T> Key<T> add(String name, Value... metadata) {
        return add(name, Record.of(metadata));
    }

    public <T> Key<T> add(String name, Record metadata) {
        Key<T> key = Key.named(name, metadata);
        return add(key);
    }

    public <T> ListKey<T> addList(String name, Value... metadata) {
        return addList(name, Record.of(metadata));
    }

    public <T> ListKey<T> addList(String name, Record metadata) {
        ListKey<T> key = ListKey.named(name, metadata);
        return add(key);
    }

    public RecordKey addRecord(String name, Value... metadata) {
        return addRecord(name, Record.of(metadata));
    }

    public RecordKey addRecord(String name, Record metadata) {
        RecordKey key = RecordKey.named(name, metadata);
        return add(key);
    }

    public <T> ValidRecordKey<T> addValidRecord(String name, Schema<T> schema, Value... metadata) {
        return addValidRecord(name, schema, Record.of(metadata));
    }

    public <T> ValidRecordKey<T> addValidRecord(String name, Schema<T> schema, Record metadata) {
        ValidRecordKey<T> key = ValidRecordKey.named(name, schema, metadata);
        return add(key);
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
