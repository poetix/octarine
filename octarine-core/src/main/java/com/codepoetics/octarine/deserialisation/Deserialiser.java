package com.codepoetics.octarine.deserialisation;

import java.io.Reader;
import java.io.StringReader;
import java.util.function.Function;

public interface Deserialiser<T, R> extends Function<T, R> {
    R fromReader(Reader reader);

    default R fromString(String input) {
        try (StringReader reader = new StringReader(input)) {
            return fromReader(reader);
        }
    }
}
