package com.codepoetics.octarine.json;

import com.codepoetics.octarine.records.Deserialiser;
import com.codepoetics.octarine.records.Injections;
import com.codepoetics.octarine.records.Record;
import com.codepoetics.octarine.records.RecordBuilder;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import org.pcollections.PVector;
import org.pcollections.TreePVector;

import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
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
            Map<String, Consumer<JsonParser>> fieldDeserialisers = new HashMap<>();

            Injections<JsonParser> injections = Injections.against(this);

            RecordBuilder<JsonParser> builder = injections.get();
            injecting(injections);

            while (parser.nextToken() != JsonToken.END_OBJECT) {
                String fieldName = parser.getCurrentName();
                builder.accept(fieldName, parser);
            }

            return builder.get();
        } catch (IOException e) {
            throw new JsonReadingException(e);
        }
    }

    default <V> PVector<V> readList(JsonParser p, Function<JsonParser, V> extractor) {
        List<V> values = new LinkedList<>();
        try {
            p.nextToken();
            while (p.getCurrentToken() != JsonToken.END_ARRAY) {
                 values.add(extractor.apply(p));
            }
            return TreePVector.from(values);
        } catch (IOException e) {
            throw new JsonReadingException(e);
        }
    }

    static Function<JsonParser, String> fromString = p -> {
        try {
            return p.nextTextValue();
        } catch (IOException e) {
            throw new JsonReadingException(e);
        }
    };

    static Function<JsonParser, Integer> fromInteger = p -> {
        try {
            return p.nextIntValue(0);
        } catch (IOException e) {
            throw new JsonReadingException(e);
        }
    };
}
