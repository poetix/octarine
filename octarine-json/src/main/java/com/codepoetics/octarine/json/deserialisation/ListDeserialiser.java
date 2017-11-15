package com.codepoetics.octarine.json.deserialisation;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonStreamContext;
import com.fasterxml.jackson.core.JsonToken;
import org.pcollections.PVector;
import org.pcollections.TreePVector;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

public final class ListDeserialiser<V> implements SafeDeserialiser<PVector<V>> {

    public static <V> ListDeserialiser<V> readingItemsWith(Function<JsonParser, ? extends V> itemDeserialiser) {
        return new ListDeserialiser<>(itemDeserialiser);
    }

    public static ListDeserialiser<String> readingStrings() {
        return readingItemsWith(Deserialisers.ofString);
    }

    public static ListDeserialiser<Integer> readingIntegers() {
        return readingItemsWith(Deserialisers.ofInteger);
    }

    public static ListDeserialiser<Long> readingLongs() {
        return readingItemsWith(Deserialisers.ofLong);
    }

    public static ListDeserialiser<Double> readingDoubles() {
        return readingItemsWith(Deserialisers.ofDouble);
    }

    public static ListDeserialiser<Boolean> readingBooleans() {
        return readingItemsWith(Deserialisers.ofBoolean);
    }

    private final Function<JsonParser, ? extends V> itemDeserialiser;

    private ListDeserialiser(Function<JsonParser, ? extends V> itemDeserialiser) {
        this.itemDeserialiser = itemDeserialiser;
    }

    @Override
    public PVector<V> applyUnsafe(JsonParser p) throws IOException {
        List<V> values = new LinkedList<>();

        /*
         * If we are in the root context (not inside an object or list) then we need to consume the next token
         * before attempting to call child deserialisers.
         */
        if (p.getParsingContext().inRoot()) {
            if (p.nextToken() == JsonToken.END_ARRAY) {
                return TreePVector.empty();
            }
        }

        if (JsonToken.VALUE_NULL != p.getCurrentToken()) {
            /*
             * When the parser has hit the end of input nextToken() will always return null.
             * So need to prevent infinite loops we check the parser closed flag.
             */
            while (!p.isClosed() && (p.nextToken() != JsonToken.END_ARRAY)) {
                values.add(itemDeserialiser.apply(p));
            }
        }
        return TreePVector.from(values);
    }
}
