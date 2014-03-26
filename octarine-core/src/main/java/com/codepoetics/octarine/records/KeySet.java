package com.codepoetics.octarine.records;

import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class KeySet implements BiConsumer<Record, Consumer<String>> {

    private final Set<Key<?>> keys = new HashSet<>();
    public <T> Key<T> add(String name, Value...metadata) {
        return add(name, Record.of(metadata));
    }

    public <T> Key<T> add(String name, Record metadata) {
        Key<T> key = Key.named(name, metadata);
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
