package com.codepoetics.octarine.json.deserialisation;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.function.Function;

public interface Deserialiser<R> extends Function<JsonParser, R> {

    default R fromReader(Reader reader) {
        JsonFactory factory = new JsonFactory();

        try (JsonParser parser = factory.createParser(reader)) {
            return apply(parser);
        } catch (IOException e) {
            throw new DeserialisationException(e);
        }
    }

    default R fromString(String input) {
        try (StringReader reader = new StringReader(input)) {
            return fromReader(reader);
        }
    }
}
