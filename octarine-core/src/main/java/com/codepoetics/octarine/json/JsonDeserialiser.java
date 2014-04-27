package com.codepoetics.octarine.json;

import com.codepoetics.octarine.serialisation.Deserialiser;
import com.codepoetics.octarine.serialisation.Injections;
import com.codepoetics.octarine.records.Record;
import com.codepoetics.octarine.serialisation.RecordBuilder;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import org.pcollections.PVector;
import org.pcollections.TreePVector;

import java.io.IOException;
import java.io.Reader;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

public interface JsonDeserialiser extends Deserialiser<JsonParser> {

    default Record readFromReader(Reader reader) throws IOException {
        JsonFactory factory = new JsonFactory();
        try (JsonParser parser = factory.createParser(reader)) {
            return readRecord(parser);
        } catch (JsonReadingException e) {
            throw (e.getIOExceptionCause());
        }
    }

    default Record readRecord(JsonParser parser) {
        try {
            parser.nextToken();

            Injections<JsonParser> injections = Injections.against(this);
            injecting(injections);
            RecordBuilder<JsonParser> builder = injections.get();

            while (parser.nextValue() != JsonToken.END_OBJECT) {
                String fieldName = parser.getCurrentName();
                builder.accept(fieldName, parser);
            }

            return builder.get();
        } catch (IOException e) {
            throw new JsonReadingException(e);
        }
    }

    default <V> PVector<V> readList(JsonParser p, Function<JsonParser, ? extends V> extractor) {
        List<V> values = new LinkedList<>();
        try {
            while (p.nextToken() != JsonToken.END_ARRAY) {
                values.add(extractor.apply(p));
            }
            return TreePVector.from(values);
        } catch (IOException e) {
            throw new JsonReadingException(e);
        }
    }

    interface SafeReader<T> extends Function<JsonParser, T> {
        default T apply(JsonParser p) {
            try {
                return applyUnsafe(p);
            } catch (IOException e) {
                throw new JsonReadingException(e);
            }
        }

        T applyUnsafe(JsonParser p) throws IOException;
    }

    static SafeReader<String> fromString = JsonParser::getValueAsString;
    static SafeReader<Integer> fromInteger = JsonParser::getIntValue;
    static SafeReader<Boolean> fromBoolean = JsonParser::getBooleanValue;
    static SafeReader<Long> fromLong = JsonParser::getLongValue;
    static SafeReader<Double> fromDouble = JsonParser::getDoubleValue;
}

