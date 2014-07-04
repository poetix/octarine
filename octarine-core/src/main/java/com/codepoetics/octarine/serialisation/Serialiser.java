package com.codepoetics.octarine.serialisation;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.function.BiConsumer;

public interface Serialiser<S, T> extends BiConsumer<S, T> {

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

    void toWriter(Writer writer, T value) throws IOException;
}
