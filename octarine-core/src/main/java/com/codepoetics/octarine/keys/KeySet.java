package com.codepoetics.octarine.keys;

import com.codepoetics.octarine.api.Key;
import com.codepoetics.octarine.api.Record;

import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class KeySet implements BiConsumer<Record, Consumer<String>> {

    private final Set<Key<?>> keys = new HashSet<>();

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
