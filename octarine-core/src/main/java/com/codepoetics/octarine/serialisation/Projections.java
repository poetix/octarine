package com.codepoetics.octarine.serialisation;

import com.codepoetics.octarine.records.Key;
import com.codepoetics.octarine.records.ListKey;
import com.codepoetics.octarine.records.Record;

import java.util.List;
import java.util.function.BiConsumer;

public interface Projections<T> {

    static <T> Projections<T> against(Serialiser<T> serialiser, Record record, T writer) {
        return new Projections<T>() {

            @Override
            public <V> Projections<T> add(Key<V> key, String keyName, BiConsumer<? super V, T> valueSerialiser) {
                key.get(record).ifPresent(v -> {
                    serialiser.writeKeyName(keyName, writer);
                    valueSerialiser.accept(v, writer);
                });
                return this;
            }

            @Override
            public <V> BiConsumer<List<V>, T> asList(BiConsumer<? super V, T> valueSerialiser) {
                return (vs, t) -> {
                    serialiser.startList(t);
                    vs.forEach(v -> valueSerialiser.accept(v, t));
                    serialiser.endList(t);
                };
            }

        };
    }


    <V> Projections<T> add(Key<V> key, String keyName, BiConsumer<? super V, T> valueSerialiser);

    default <V> Projections<T> add(Key<V> key, BiConsumer<? super V, T> valueSerialiser) {
        return add(key, key.name(), valueSerialiser);
    }

    default <V> Projections<T> addList(ListKey<V> key, String keyName, BiConsumer<? super V, T> valueSerialiser) {
        return add(key, keyName, asList(valueSerialiser));
    }

    default <V> Projections<T> addList(ListKey<V> key, BiConsumer<? super V, T> valueSerialiser) {
        return addList(key, key.name(), valueSerialiser);
    }

    <V> BiConsumer<List<V>, T> asList(BiConsumer<? super V, T> valueSerialiser);
}
