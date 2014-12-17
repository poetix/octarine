package com.codepoetics.octarine.json;

import com.codepoetics.octarine.deserialisation.Deserialiser;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;

import java.io.IOException;
import java.io.Reader;

public interface JsonDeserialiser<R> extends Deserialiser<JsonParser, R> {
    @Override
    default R fromReader(Reader reader) {
        JsonFactory factory = new JsonFactory();

        try (JsonParser parser = factory.createParser(reader)) {
            return apply(parser);
        } catch (IOException e) {
            throw new JsonDeserialisationException(e);
        }
    }
}
