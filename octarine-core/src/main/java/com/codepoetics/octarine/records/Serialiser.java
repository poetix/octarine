package com.codepoetics.octarine.records;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

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

    void projecting(Projections<T> projections);

    void startRecord(T writer);
    void endRecord(T writer);
    void nextProjection(T writer);

    default void accept(Record record, T writer) {
        writeRecord(record, writer);
    }

    default void writeRecord(Record record, T writer) {
        startRecord(writer);
        projecting(new Projections<T>() {
            private boolean first = true;
            @Override
            public void accept(BiConsumer<Record, T> projector) {
                if (first) { first = false; } else { nextProjection(writer); }
                projector.accept(record, writer);
            }
            @Override
            public void writeKeyName(Key<?> key, String keyName, T writer) {
                Serialiser.this.writeKeyName(key, keyName, writer);
            }
        });
        endRecord(writer);
    }

    void writeKeyName(Key<?> key, String keyName, T writer);

    void startList(T writer);
    void endList(T writer);
    void nextValue(T writer);

    default <V> void writeList(List<V> values, BiConsumer<V, T> valueSerialiser, T writer) {
        startList(writer);
        values.forEach(new Consumer<V>() {
            private boolean first = true;
            @Override
            public void accept(V v) {
                if (first) { first = false; } else { nextValue(writer); }
                writeValue(v, valueSerialiser, writer);
            }
        });
        endList(writer);
    }

    default <V> void writeValue(V value, BiConsumer<V, T> valueSerialiser, T writer) {
        valueSerialiser.accept(value, writer);
    }

    default <V> BiConsumer<List<V>, T> asList(BiConsumer<V, T> valueSerialiser) {
        return (vs, t) -> {
            Serialiser.this.<V>writeList(vs, valueSerialiser, t);
        };
    }

    default <V1, V2, T> BiConsumer<V1, T> map(Function<V1, V2> f, BiConsumer<V2, T> consumer) {
        return (v1, t) -> consumer.accept(f.apply(v1), t);
    }

    interface Bound<T> {
        void writeRecord(Record record);
        <V> void writeList(List<V> values, BiConsumer<V, T> valueSerialiser);
        <V> void writeValue(V value, BiConsumer<V, T> valueSerialiser);
    }

    default Bound<T> using(T writer) {
        return new Bound<T>() {

            @Override
            public void writeRecord(Record record) {
                Serialiser.this.writeRecord(record, writer);
            }

            @Override
            public <V> void writeList(List<V> values, BiConsumer<V, T> valueSerialiser) {
                Serialiser.this.writeList(values, valueSerialiser, writer);
            }

            @Override
            public <V> void writeValue(V value, BiConsumer<V, T> valueSerialiser) {
                 Serialiser.this.writeValue(value, valueSerialiser, writer);
            }
        };
    }
}
