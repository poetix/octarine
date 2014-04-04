package com.codepoetics.octarine.records;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;
import java.util.Optional;
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

    void projecting(Projections<T> projections);

    void startRecord(T writer);
    void endRecord(T writer);
    void writeKeyName(String keyName, T writer);

    default void accept(Record record, T writer) {
        Serialiser<T> serialiser = this;
        startRecord(writer);

        projecting(new Projections<T>() {
            @Override
            public void accept(BiConsumer<Record, T> projector) {
                projector.accept(record, writer);
            }

            @Override
            public <V> Projections<T> add(Key<V> key, String keyName, BiConsumer<? super V, T> valueSerialiser) {
                return apply((r, t) -> {
                        Optional<V> value = key.from(r);
                        if (value.isPresent()) {
                            serialiser.writeKeyName(keyName, t);
                            valueSerialiser.accept(value.get(), t);
                        }
                    });
            }

            @Override
            public <V> BiConsumer<List<V>, T> asList(BiConsumer<V, T> valueSerialiser) {
                return (vs, t) -> {
                    serialiser.startList(t);
                    vs.forEach(v -> valueSerialiser.accept(v, writer));
                    serialiser.endList(t);
                };
            }
        });
        endRecord(writer);
    }

    void startList(T writer);
    void endList(T writer);
}
