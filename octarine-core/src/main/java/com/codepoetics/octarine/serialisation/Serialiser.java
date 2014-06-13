package com.codepoetics.octarine.serialisation;

import com.codepoetics.octarine.records.Record;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.function.BiConsumer;

public interface Serialiser<T> extends BiConsumer<Record, T> {

    default String toString(Record record) {
        StringWriter writer = new StringWriter();
        try {
            toWriter(record, writer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        writer.flush();
        return writer.toString();
    }

    void toWriter(Record record, Writer writer) throws IOException;

    void startRecord(T writer);
    void endRecord(T writer);
    void writeKeyName(String keyName, T writer);

    void startList(T writer);
    void endList(T writer);

    default void accept(Record record, T writer) {
        startRecord(writer);

        projecting(Projections.against(this, record, writer));

        endRecord(writer);
    }

    void projecting(Projections<T> projections);

}
