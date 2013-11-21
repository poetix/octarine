package com.codepoetics.octarine.lenses;

import com.codepoetics.octarine.morphisms.Bijection;
import org.pcollections.PMap;
import org.pcollections.PVector;

import java.util.Arrays;
import java.util.function.BiFunction;
import java.util.function.Function;

public interface Lens<T, V> extends Function<T, Lensed<T, V>> {

    static <T, V> Lens<T, V> of(Function<T, V> getter, BiFunction<T, V, T> setter) {
        return new Lens<T, V>() {
            @Override
            public Lensed<T, V> apply(T t) {
                return new Lensed<T, V>() {

                    @Override
                    public T apply(V v) {
                        return setter.apply(t, v);
                    }

                    @Override
                    public V get() {
                        return getter.apply(t);
                    }
                };
            }
        };
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

    default <V2> Lens<T, V2> join(Function<V, Lensed<V, V2>> next) {
        return of(
            t -> next.apply(apply(t).get()).get(),
            (t, v) -> {
                Lensed<T, V> left = apply(t);
                Lensed<V, V2> right = next.apply(left.get());
                return left.apply(right.apply(v));
            }
        );
    }

    default <V2> Lens<T, V2> andThen(Bijection<V, V2> bijection) {
        return of(
            t -> bijection.in(Lens.this.apply(t).get()),
            (t, v) -> Lens.this.apply(t).apply(bijection.out(v))
        );
    }

    default <T2> Lens<T2, V> compose(Bijection<T, T2> bijection) {
        return of(
            t -> Lens.this.apply(bijection.out(t)).get(),
            (t, v) -> bijection.in(Lens.this.apply(bijection.out(t)).apply(v))
        );
    }
}
