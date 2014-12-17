package com.codepoetics.octarine.json;

import com.codepoetics.octarine.serialisation.Serialiser;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;

import java.io.IOException;
import java.io.Writer;

public interface JsonSerialiser<T> extends Serialiser<JsonGenerator, T> {

    @Override
    default void toWriter(Writer writer, T value) throws IOException {
        try (JsonGenerator jsonWriter = new JsonFactory().createGenerator(writer)) {
            accept(jsonWriter, value);
            jsonWriter.flush();
        } catch (JsonWritingException e) {
            throw e.getIOExceptionCause();
        }
    }

}
