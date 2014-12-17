package com.codepoetics.octarine.json;

import com.codepoetics.octarine.deserialisation.ParserMapper;
import com.codepoetics.octarine.records.Record;
import com.codepoetics.octarine.records.Value;
import com.codepoetics.octarine.validation.Valid;
import com.codepoetics.octarine.validation.Validation;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import org.pcollections.HashTreePMap;
import org.pcollections.PMap;
import org.pcollections.PVector;
import org.pcollections.TreePVector;

import java.io.IOException;
import java.util.*;
import java.util.function.Function;

public final class JsonDeserialisers {
    public static final SafeDeserialiser<String> fromString = JsonParser::getValueAsString;
    public static final SafeDeserialiser<Integer> fromInteger = JsonParser::getIntValue;
    public static final SafeDeserialiser<Boolean> fromBoolean = JsonParser::getBooleanValue;
    public static final SafeDeserialiser<Long> fromLong = JsonParser::getLongValue;
    public static final SafeDeserialiser<Double> fromDouble = JsonParser::getDoubleValue;

    private JsonDeserialisers() {
    }

    public static <V> SafeDeserialiser<PVector<V>> fromList(Function<JsonParser, ? extends V> extractor) {
        return p -> {
            List<V> values = new LinkedList<>();
            while (p.nextToken() != JsonToken.END_ARRAY) {
                values.add(extractor.apply(p));
            }
            return TreePVector.from(values);
        };
    }

    public static <V> SafeDeserialiser<PMap<String, V>> fromMap(Function<JsonParser, ? extends V> extractor) {
        return p -> {
            Map<String, V> values = new HashMap<>();

            if (p.nextToken() == JsonToken.END_OBJECT) {
                return HashTreePMap.empty();
            }
            while (p.nextValue() != JsonToken.END_OBJECT) {
                String fieldName = p.getCurrentName();
                values.put(fieldName, extractor.apply(p));
            }

            return HashTreePMap.from(values);
        };
    }

    public static SafeDeserialiser<Record> readingKeys(ParserMapper<JsonParser> mapper) {
        return parser -> {
            List<Value> values = new ArrayList<>();

            if (parser.nextToken() == JsonToken.END_OBJECT) {
                return Record.empty();
            }

            while (parser.nextValue() != JsonToken.END_OBJECT) {
                String fieldName = parser.getCurrentName();
                mapper.getValue(fieldName, parser).ifPresent(values::add);
            }

            return Record.of(values);
        };
    }

    public static <S> SafeDeserialiser<Valid<S>> fromValid(Function<JsonParser, ? extends Validation<S>> extractor) {
        return parser -> extractor.apply(parser).get();
    }

    public static interface SafeDeserialiser<S> extends JsonDeserialiser<S> {
        default S apply(JsonParser p) {
            try {
                return applyUnsafe(p);
            } catch (IOException e) {
                throw new JsonDeserialisationException(e);
            }
        }

        S applyUnsafe(JsonParser p) throws IOException;
    }
}
