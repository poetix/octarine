package com.codepoetics.octarine.validation.api;

import com.codepoetics.octarine.api.Record;
import com.codepoetics.octarine.api.Value;
import com.codepoetics.octarine.functional.extractors.Extractor;
import com.codepoetics.octarine.records.HashRecord;
import com.codepoetics.octarine.validation.ValidRecord;

import java.util.LinkedList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@FunctionalInterface
public interface Schema<T> extends BiConsumer<Record, Consumer<String>>, Extractor.FromPredicate<Record, Valid<T>> {

    @Override
    default boolean test(Record record) {
        return validate(record).isValid();
    }

    @Override
    default Valid<T> extract(Record record) {
        return validate(record).get();
    }

    default Validation<T> validate(Value... values) {
        return validate(HashRecord.of(values));
    }

    default Validation<T> validate(Record record) {
        List<String> validationErrors = new LinkedList<>();
        accept(record, validationErrors::add);
        if (validationErrors.isEmpty()) {
            return Validation.valid(new ValidRecord<>(record, this));
        }
        return Validation.invalid(validationErrors);
    }

}
