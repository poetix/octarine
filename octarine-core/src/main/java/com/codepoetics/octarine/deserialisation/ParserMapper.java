package com.codepoetics.octarine.deserialisation;

import com.codepoetics.octarine.records.Value;

public interface ParserMapper<T> {
    boolean hasKeyFor(String fieldName);

    Value getValue(String fieldName, T parser);
}
