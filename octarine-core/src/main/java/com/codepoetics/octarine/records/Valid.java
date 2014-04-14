package com.codepoetics.octarine.records;

import org.pcollections.PMap;

import java.util.Set;

public interface Valid<T> extends Record {

    Schema<T> schema();

    default Validation<T> validateWith(Value...values) {
        return schema().validate(with(values));
    }

    default Validation<T> validateWith(PMap<Key<?>, Object> values) {
        return schema().validate(with(values));
    }

    default Validation<T> validateWith(Record other) {
        return schema().validate(with(other));
    }

    default Validation<T> validateWithout(Key<?>...keys) {
        return schema().validate(without(keys));
    }

    default Validation<T> validateWithout(Set<Key<?>> keys) {
        return schema().validate(without(keys));
    }
}
