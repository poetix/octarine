package com.codepoetics.octarine.json.serialisation;

import com.fasterxml.jackson.core.JsonGenerator;

import java.io.IOException;

public interface SafeSerialiser<T> extends Serialiser<T> {
    default void accept(JsonGenerator j, T value) {
        try {
            unsafeAccept(j, value);
        } catch (IOException e) {
            throw new SerialisationException(e);
        }
    }

    void unsafeAccept(JsonGenerator j, T value) throws IOException;
}
