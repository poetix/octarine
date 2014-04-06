package com.codepoetics.octarine.records;

import org.pcollections.PVector;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.function.Function;

public interface Deserialiser<T> extends Function<T, Record> {

    default Record readFromString(String input) {
        StringReader reader = new StringReader(input);
        try {
            return readFromReader(reader);
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    default <T> Valid<T> readFromString(String input, Schema<T> schema) {
        return schema.validate(readFromString(input)).get();
    }

    Record readFromReader(Reader reader) throws IOException;

    default <T> Valid<T> readFromReader(Reader reader, Schema<T> schema) throws IOException {
        return schema.validate(readFromReader(reader)).get();
    }

    void injecting(Injections<T> injections);

    default Record apply(T reader) {
        return readRecord(reader);
    }

    Record readRecord(T reader);
    <V> PVector<V> readList(T reader, Function<T, V> extractor);

    default <R> Function<T, Valid<R>> validAgainst(Schema<R> schema) {
        return t -> {
            Record record = apply(t);
            return schema.validate(record).get();
        };
    }
}
