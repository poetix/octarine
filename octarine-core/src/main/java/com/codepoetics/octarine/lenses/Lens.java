package com.codepoetics.octarine.lenses;

import com.codepoetics.octarine.morphisms.Bijection;
import com.codepoetics.octarine.tuples.Pair;
import com.codepoetics.octarine.tuples.Pairable;
import org.pcollections.PMap;
import org.pcollections.PVector;

import java.util.Arrays;
import java.util.function.BiFunction;
import java.util.function.Function;

public interface Lens<T, V> extends Function<T, V> {

    public static <T, V> Lens<T, V> of(Function<T, V> getter, BiFunction<T, V, T> setter) {
        return new Lens<T, V>() {

            @Override
            public V get(T instance) {
                return getter.apply(instance);
            }

            @Override
            public T set(T instance, V newValue) {
                return setter.apply(instance, newValue);
            }
        };
    }

    V get(T instance);
    default V apply(T instance) { return get(instance); }
    T set(T instance, V newValue);

    default BoundLens<T, V> bind(T instance) {
        return BoundLens.binding(get(instance), v -> this.set(instance, v));
    }

    default T update(T instance, Function<V, V> updater) {
        return set(instance, updater.apply(get(instance)));
    }

    default Function<T, T> inject(V newValue) {
        return t -> set(t, newValue);
    }

    default Function<T, T> inflect(Function<V, V> updater) {
        return t -> update(t, updater);
    }

    static <T> Lens<T[], T> intoArray(int index) {
        return of(
           ts -> ts[index],
            (ts, t) -> {
                T[] copy = Arrays.copyOf(ts, ts.length);
                copy[index] = t;
                return copy;
            }
        );
    }

    static <K, V> Lens<PMap<K, V>, V> intoPMap(K key) {
        return of(
            m -> m.get(key),
            (m, v) -> m.plus(key, v)
        );
    }

    static <T> Lens<PVector<T>, T> intoPVector(int index) {
        return of(
             ts -> ts.get(index),
             (ts, t) -> ts.plus(index, t)
        );
    }

    default <V2> Lens<T, V2> join(Lens<V, V2> next) {
        return of(
            t -> next.get(get(t)),
            (t, v) -> set(t, next.set(get(t), v))
        );
    }

    default <V2> OptionalLens<T, V2> join(OptionalLens<V, V2> next) {
        return OptionalLens.wrap(of(
            t -> next.get(this.get(t)),
            (t, v) -> set(t, next.set(this.get(t), v))
        ));
    }

    default <V2> Lens<T, V2> under(Bijection<V, V2> bijection) {
        return of(
            t -> bijection.apply(get(t)),
            (t, v) -> set(t, bijection.reverse().apply(v))
        );
    }

    default <T0> Lens<T0, V> over(Bijection<T0, T> bijection) {
        return of(
                t -> get(bijection.apply(t)),
                (t, v) -> bijection.reverse().apply(set(bijection.apply(t), v))
        );
    }
}
