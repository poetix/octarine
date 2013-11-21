package com.codepoetics.octarine.lenses;

import java.util.function.Function;
import java.util.function.Supplier;

public interface Lensed<T, V> extends Supplier<V>, Function<V, T> {

    default T update(Function<V, V> updater) {
        return apply(updater.apply(get()));
    }
}
