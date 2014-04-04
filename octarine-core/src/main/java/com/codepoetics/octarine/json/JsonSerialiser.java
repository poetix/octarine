package com.codepoetics.octarine.json;

import com.codepoetics.octarine.records.Key;
import com.codepoetics.octarine.records.Record;
import com.codepoetics.octarine.records.Serialiser;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;

import java.io.IOException;
import java.io.Writer;
import java.util.function.BiConsumer;

public interface JsonSerialiser extends Serialiser<JsonGenerator> {

    @Override default void toWriter(Record record, Writer writer) throws IOException {
        JsonGenerator jsonWriter = new JsonFactory().createGenerator(writer);
        try {
            writeRecord(record, jsonWriter);
        } catch (JsonWritingException e) {
            e.throwCause();
        }
        jsonWriter.flush();
    }

    @Override default void startRecord(JsonGenerator writer) {
        try {
            writer.writeStartObject();
        } catch (IOException e) {
            throw new JsonWritingException(e);
        }
    }

    @Override default void nextProjection(JsonGenerator writer) {
        // Do nothing
    }

    @Override default void endRecord(JsonGenerator writer) {
        try {
            writer.writeEndObject();
        } catch (IOException e) {
            throw new JsonWritingException(e);
        }
    }

    @Override default void startList(JsonGenerator writer) {
        try {
            writer.writeStartArray();
        } catch (IOException e) {
            throw new JsonWritingException(e);
        }
    }

    @Override default void nextValue(JsonGenerator writer) {
        // Do nothing
    }

    @Override default void endList(JsonGenerator writer) {
        try {
            writer.writeEndArray();
        } catch (IOException e) {
            throw new JsonWritingException(e);
        }
    }

    @Override default void writeKeyName(Key<?> key, String keyName, JsonGenerator writer) {
        try {
            writer.writeFieldName(keyName);
        } catch (IOException e) {
            throw new JsonWritingException(e);
        }
    }

    static BiConsumer<String, JsonGenerator> asString =
        (s, j) -> {
            try {
                j.writeString(s);
            } catch (IOException e) {
                throw new JsonWritingException(e);
            }
    };

    static BiConsumer<Boolean, JsonGenerator> asBoolean =
            (b, j) -> {
                try {
                    j.writeBoolean(b);
                } catch (IOException e) {
                    throw new JsonWritingException(e);
                }
            };

    static BiConsumer<Integer, JsonGenerator> asInteger =
            (i, j) -> {
                try {
                    j.writeNumber(i);
                } catch (IOException e) {
                    throw new JsonWritingException(e);
                }
            };


}
