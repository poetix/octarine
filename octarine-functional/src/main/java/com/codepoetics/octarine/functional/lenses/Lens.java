package com.codepoetics.octarine.functional.lenses;

import org.pcollections.PMap;
import org.pcollections.PVector;

import java.util.Arrays;
import java.util.function.BiFunction;
import java.util.function.Function;

public interface Lens<T, V> extends LensLike<T, V, Focus<T, V>> {

    public static <T, V> Lens<T, V> of(Function<T, V> getter, BiFunction<T, V, T> setter) {
        return new Lens<T, V>() {
            @Override
            public V get(T target) {
                return getter.apply(target);
            }

            @Override
            public T set(T target, V newValue) {
                return setter.apply(target, newValue);
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
                (ts, t) -> ts.with(index, t)
        );
    }

    default Focus<T, V> into(T target) {
        return Focus.of(() -> get(target), v -> set(target, v));
    }

}
