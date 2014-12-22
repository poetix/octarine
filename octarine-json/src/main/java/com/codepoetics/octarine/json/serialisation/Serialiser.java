package com.codepoetics.octarine.json.serialisation;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.function.BiConsumer;

public interface Serialiser<T> extends BiConsumer<JsonGenerator, T> {

    default String toString(T value) {
        StringWriter writer = new StringWriter();
        try {
            toWriter(writer, value);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        writer.flush();
        return writer.toString();
    }

    default void toWriter(Writer writer, T value) throws IOException {
        try (JsonGenerator jsonWriter = new JsonFactory().createGenerator(writer)) {
            accept(jsonWriter, value);
            jsonWriter.flush();
        } catch (SerialisationException e) {
            throw e.getIOExceptionCause();
        }
    }

}
