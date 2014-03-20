package com.codepoetics.octarine.lenses;

import java.util.function.Function;
import java.util.function.Supplier;

public class BoundLens<T, V> implements Supplier<V>, Function<V, T> {

    public static <T, V> BoundLens<T, V> binding(T instance, Lens<T, V> lens) {
        return new BoundLens<T, V>(instance, lens);
    }

    private final T instance;
    private final Lens<T, V> lens;

    private BoundLens(T instance, Lens<T, V> lens) {
        this.instance = instance;
        this.lens = lens;
    }

    public V get() { return lens.get(instance); }
    public T apply(V value) { return lens.set(instance, value); }

    public T update(Function<V, V> updater) {
        return apply(updater.apply(get()));
    }

    public BoundLens<T, V> retarget(T instance) {
        return new BoundLens<T, V>(instance, lens);
    }
}
