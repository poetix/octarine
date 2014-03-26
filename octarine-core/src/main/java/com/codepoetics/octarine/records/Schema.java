package com.codepoetics.octarine.records;

import org.pcollections.PMap;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public interface Schema<T> extends BiConsumer<Record, Consumer<String>> {

    default Validation<T> validate(Value...values) {
        return validate(Record.of(values));
    }

    default Validation<T> validate(Record record) {
        List<String> validationErrors = new LinkedList<>();
        accept(record, validationErrors::add);
        if (validationErrors.isEmpty()) {
            return Validation.valid(new Valid<T>() {
                @Override
                public <S> Optional<S> get(KeyLike<S> key) {
                    return record.get(key);
                }

                @Override
                public PMap<Key<?>, Object> values() {
                    return record.values();
                }

                @Override public int hashCode() { return record.hashCode(); }
                @Override public boolean equals(Object other) { return record.equals(other); }
            });
        }
        return Validation.invalid(validationErrors);
    }
}
