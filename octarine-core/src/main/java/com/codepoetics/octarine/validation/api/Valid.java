package com.codepoetics.octarine.validation.api;

import com.codepoetics.octarine.api.Key;
import com.codepoetics.octarine.api.Record;
import com.codepoetics.octarine.api.Value;

import java.util.Collection;

public interface Valid<T> extends Record {

    Schema<T> schema();

    default Validation<T> validateWith(Value... values) {
        return schema().validate(with(values));
    }

    default Validation<T> validateWith(Record other) {
        return schema().validate(with(other));
    }

    default Validation<T> validateWithout(Key<?>... keys) {
        return schema().validate(without(keys));
    }

    default Validation<T> validateWithout(Collection<Key<?>> keys) {
        return schema().validate(without(keys));
    }

}
