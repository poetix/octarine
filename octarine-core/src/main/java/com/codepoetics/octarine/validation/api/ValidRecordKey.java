package com.codepoetics.octarine.validation.api;

import com.codepoetics.octarine.api.Key;
import com.codepoetics.octarine.api.Value;

public interface ValidRecordKey<T> extends Key<Valid<T>> {

    Schema<T> schema();

    default Value of(Value... values) {
        return of(schema().validate(values).get());
    }
}
