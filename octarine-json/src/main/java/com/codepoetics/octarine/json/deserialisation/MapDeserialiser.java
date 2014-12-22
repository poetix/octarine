package com.codepoetics.octarine.json.deserialisation;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import org.pcollections.HashTreePMap;
import org.pcollections.PMap;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public final class MapDeserialiser<V> implements SafeDeserialiser<PMap<String, V>> {

    public static <V> MapDeserialiser<V> readingValuesWith(Function<JsonParser, ? extends V> valueDeserialiser) {
        return new MapDeserialiser<>(valueDeserialiser);
    }

    public static MapDeserialiser<String> readingStrings() {
        return readingValuesWith(Deserialisers.ofString);
    }

    public static MapDeserialiser<Integer> readingIntegers() {
        return readingValuesWith(Deserialisers.ofInteger);
    }

    public static MapDeserialiser<Long> readingLongs() {
        return readingValuesWith(Deserialisers.ofLong);
    }

    public static MapDeserialiser<Double> readingDoubles() {
        return readingValuesWith(Deserialisers.ofDouble);
    }

    public static MapDeserialiser<Boolean> readingBooleans() {
        return readingValuesWith(Deserialisers.ofBoolean);
    }
    
    private final Function<JsonParser, ? extends V> valueDeserialiser;

    private MapDeserialiser(Function<JsonParser, ? extends V> valueDeserialiser) {
        this.valueDeserialiser = valueDeserialiser;
    }

    @Override
    public PMap<String, V> applyUnsafe(JsonParser p) throws IOException {
        Map<String, V> values = new HashMap<>();

        if (p.nextToken() == JsonToken.END_OBJECT) {
            return HashTreePMap.empty();
        }
        while (p.nextValue() != JsonToken.END_OBJECT) {
            String fieldName = p.getCurrentName();
            values.put(fieldName, valueDeserialiser.apply(p));
        }

        return HashTreePMap.from(values);
    }
}
