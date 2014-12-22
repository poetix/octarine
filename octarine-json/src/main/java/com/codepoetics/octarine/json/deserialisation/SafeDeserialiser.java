package com.codepoetics.octarine.json.deserialisation;

import com.fasterxml.jackson.core.JsonParser;

import java.io.IOException;

public interface SafeDeserialiser<S> extends Deserialiser<S> {
    default S apply(JsonParser p) {
        try {
            return applyUnsafe(p);
        } catch (IOException e) {
            throw new DeserialisationException(e);
        }
    }

    S applyUnsafe(JsonParser p) throws IOException;
}
