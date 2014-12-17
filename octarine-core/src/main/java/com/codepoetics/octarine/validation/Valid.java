package com.codepoetics.octarine.validation;

import com.codepoetics.octarine.keys.Key;
import com.codepoetics.octarine.records.Record;
import com.codepoetics.octarine.keys.Value;
import org.pcollections.PMap;

import java.util.List;
import java.util.Set;

public interface Valid<T> extends Record {

    Schema<T> schema();

    default Validation<T> validateWith(Value... values) {
        return schema().validate(with(values));
    }

    default Validation<T> validateWith(PMap<Key<?>, Object> values) {
        return schema().validate(with(values));
    }

    default Validation<T> validateWith(Record other) {
        return schema().validate(with(other));
    }

    default Validation<T> validateWithout(Key<?>... keys) {
        return schema().validate(without(keys));
    }

    default Validation<T> validateWithout(Set<Key<?>> keys) {
        return schema().validate(without(keys));
    }

    class RecordValidationException extends RuntimeException {

        private final List<String> validationErrors;

        public RecordValidationException(List<String> validationErrors) {
            this.validationErrors = validationErrors;
        }

        public <R> Validation<R> toValidation() {
            return Validation.invalid(validationErrors);
        }
    }
}
