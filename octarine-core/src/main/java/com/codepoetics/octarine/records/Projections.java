package com.codepoetics.octarine.records;

import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public interface Projections<T> extends Function<BiConsumer<Record, T>, Projections<T>>, Consumer<BiConsumer<Record, T>> {

    static <T> Projections<T> against(Serialiser<T> serialiser, Record record, T writer) {
        return new Projections<T>() {
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
                    vs.forEach(v -> valueSerialiser.accept(v, t));
                    serialiser.endList(t);
                };
            }

        };
    }

    <V> Projections<T> add(Key<V> key, String keyName, BiConsumer<? super V, T> valueSerialiser);
    <V> BiConsumer<List<V>, T> asList(BiConsumer<V, T> valueSerialiser);

    default Projections<T> apply(BiConsumer<Record, T> projection) {
        accept(projection);
        return this;
    }

    default <V> Projections<T> add(Key<V> key, BiConsumer<? super V, T> valueSerialiser) {
        return add(key, key.name(), valueSerialiser);
    }

    default <V1, V2> BiConsumer<V1, T> map(Function<V1, V2> f, BiConsumer<V2, T> consumer) {
        return (v1, t) -> consumer.accept(f.apply(v1), t);
    }

}
