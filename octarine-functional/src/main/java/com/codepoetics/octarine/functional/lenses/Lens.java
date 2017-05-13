package com.codepoetics.octarine.functional.lenses;

import org.pcollections.PMap;
import org.pcollections.PVector;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

public interface Lens<T, V> {

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

    V get(T instance);

    T set(T instance, V newValue);

    default OptionalLens<T, V> asOptional() {
        return OptionalLens.wrap(Lens.<T, Optional<V>>of(
                t -> Optional.ofNullable(get(t)),
                (t, v) -> set(t, v.orElse(null))
        ));
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

    default <V2> Lens<T, V2> join(Lens<V, V2> next) {
        return Lens.of(
                t -> next.get(get(t)),
                (t, v) -> set(t, next.set(get(t), v))
        );
    }

    default <V2> OptionalLens<T, V2> join(OptionalLens<V, V2> next) {
        return OptionalLens.of(
                t -> next.get(get(t)),
                (t, v) -> set(t, next.set(get(t), v))
        );
    }

    /**
     * Used to construct a lens that always returns the given value.
     * When set method is used the supplied value is returned unchanged.
     */
    static <T, V2> Lens<T, V2> constant(V2 value) {
        return Lens.of(
                t -> value,
                (t, v) -> t
        );
    }
}
