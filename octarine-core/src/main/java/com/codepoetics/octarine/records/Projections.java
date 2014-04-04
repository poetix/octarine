package com.codepoetics.octarine.records;

import java.util.List;
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

    <V> Projections<T> add(Key<V> key, String keyName, BiConsumer<? super V, T> valueSerialiser);

    default <V1, V2> BiConsumer<V1, T> map(Function<V1, V2> f, BiConsumer<V2, T> consumer) {
        return (v1, t) -> consumer.accept(f.apply(v1), t);
    }

    <V> BiConsumer<List<V>, T> asList(BiConsumer<V, T> valueSerialiser);
}
