package com.codepoetics.octarine.records;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public interface Projections<T> extends Function<BiConsumer<Record, T>, Projections<T>>, Consumer<BiConsumer<Record, T>> {
    default Projections<T> apply(BiConsumer<Record, T> projection) {
        accept(projection);
        return this;
    }

    default <V> Projections<T> add(Key<V> key, BiConsumer<? super V, T> valueSerialiser) {
        return add(key, key.name(), valueSerialiser);
    }

    default <V> Projections<T> add(Key<V> key, String keyName, BiConsumer<? super V, T> valueSerialiser) {
        return apply((r, t) -> {
            Optional<V> value = key.from(r);
            if (value.isPresent()) {
                writeKeyName(key, keyName, t);
                valueSerialiser.accept(value.get(), t);
            }
        });
    }

    void writeKeyName(Key<?> key, String keyName, T writer);
}
