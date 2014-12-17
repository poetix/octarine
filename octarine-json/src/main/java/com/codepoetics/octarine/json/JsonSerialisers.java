package com.codepoetics.octarine.json;

import com.codepoetics.octarine.api.Record;
import com.codepoetics.octarine.serialisation.GeneratorMapper;
import com.fasterxml.jackson.core.JsonGenerator;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.function.Consumer;

public final class JsonSerialisers {
    private JsonSerialisers() { }

    public static interface SafeSerialiser<T> extends JsonSerialiser<T> {
        default void accept(JsonGenerator j, T value) {
            try {
                unsafeAccept(j, value);
            } catch (IOException e) {
                throw new JsonWritingException(e);
            }
        }

        void unsafeAccept(JsonGenerator j, T value) throws IOException;
    }

    public static final SafeSerialiser<String> asString = JsonGenerator::writeString;
    public static final SafeSerialiser<Integer> asInteger = JsonGenerator::writeNumber;
    public static final SafeSerialiser<Boolean> asBoolean = JsonGenerator::writeBoolean;

    public static <T> SafeSerialiser<Collection<T>> asArray(JsonSerialiser<T> itemSerialiser) {
        return (j, ts) -> {
            j.writeStartArray();
            ts.forEach(t -> itemSerialiser.accept(j, t));
            j.writeEndArray();
        };
    }

    public static <T> SafeSerialiser<Map<String, ? extends T>> asMap(JsonSerialiser<T> itemSerialiser) {
        return (j, ts) -> {
            j.writeStartObject();
            SafeSerialiser<Map.Entry<String, ? extends T>> entrySafeSerialiser = (j2, e) -> {
                j2.writeFieldName(e.getKey());
                itemSerialiser.accept(j2, e.getValue());
            };
            ts.entrySet().forEach(e -> {
                entrySafeSerialiser.accept(j, e);
            });
            j.writeEndObject();
        };
    }

    public static <T> SafeSerialiser<Record> writingKeys(GeneratorMapper<JsonGenerator> mapper) {
        return (j, r) -> {
            j.writeStartObject();
            Consumer<String> fieldNameWriter = s -> {
                try {
                    j.writeFieldName(s);
                } catch (IOException e) {
                    throw new JsonWritingException(e);
                }
            };
            mapper.generateValues(j, fieldNameWriter, k -> k.get(r));
            j.writeEndObject();
        };
    }

}
