package com.codepoetics.octarine.json.serialisation;

import com.fasterxml.jackson.core.JsonGenerator;

import java.io.IOException;
import java.util.Collection;

public final class ListSerialiser<T> implements SafeSerialiser<Collection<? extends T>> {
    public static <T> ListSerialiser<T> writingItemsWith(Serialiser<? super T> itemSerialiser) {
        return new ListSerialiser<>(itemSerialiser);
    }

    private final Serialiser<? super T> itemSerialiser;

    private ListSerialiser(Serialiser<? super T> itemSerialiser) {
        this.itemSerialiser = itemSerialiser;
    }

    @Override
    public void unsafeAccept(JsonGenerator j, Collection<? extends T> ts) throws IOException {
        j.writeStartArray();
        ts.forEach(t -> itemSerialiser.accept(j, t));
        j.writeEndArray();
    }
}
