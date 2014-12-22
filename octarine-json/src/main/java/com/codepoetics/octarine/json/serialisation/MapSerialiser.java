package com.codepoetics.octarine.json.serialisation;

import com.fasterxml.jackson.core.JsonGenerator;

import java.io.IOException;
import java.util.Map;

public final class MapSerialiser<T> implements SafeSerialiser<Map<String, ? extends T>> {

    public static <T> MapSerialiser<T> writingValuesWith(Serialiser<? super T> valueSerialiser) {
        return new MapSerialiser<>(valueSerialiser);
    }

    private final Serialiser<? super T> valueSerialiser;

    private MapSerialiser(Serialiser<? super T> valueSerialiser) {
        this.valueSerialiser = valueSerialiser;
    }

    @Override
    public void unsafeAccept(JsonGenerator j, Map<String, ? extends T> ts) throws IOException {
        j.writeStartObject();
        SafeSerialiser<Map.Entry<String, ? extends T>> entrySafeSerialiser = (j2, e) -> {
            j2.writeFieldName(e.getKey());
            valueSerialiser.accept(j2, e.getValue());
        };
        ts.entrySet().forEach(e -> {
            entrySafeSerialiser.accept(j, e);
        });
        j.writeEndObject();
    }
}
