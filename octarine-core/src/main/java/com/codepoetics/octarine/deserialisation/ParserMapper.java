package com.codepoetics.octarine.deserialisation;

import com.codepoetics.octarine.records.Value;

import java.util.Optional;

public interface ParserMapper<T> {
    Optional<Value> getValue(String fieldName, T parser);
}
