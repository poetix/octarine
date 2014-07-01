package com.codepoetics.octarine.json;

import com.fasterxml.jackson.core.JsonGenerator;

import java.io.IOException;
import java.util.function.BiConsumer;

public final class JsonSerialisers {
    private JsonSerialisers() { }

    public static final BiConsumer<String, JsonGenerator> asString =
            (s, j) -> {
                try {
                    j.writeString(s);
                } catch (IOException e) {
                    throw new JsonWritingException(e);
                }
            };
    public static final BiConsumer<Boolean, JsonGenerator> asBoolean =
            (b, j) -> {
                try {
                    j.writeBoolean(b);
                } catch (IOException e) {
                    throw new JsonWritingException(e);
                }
            };
    public static final BiConsumer<Integer, JsonGenerator> asInteger =
            (i, j) -> {
                try {
                    j.writeNumber(i);
                } catch (IOException e) {
                    throw new JsonWritingException(e);
                }
            };
}
