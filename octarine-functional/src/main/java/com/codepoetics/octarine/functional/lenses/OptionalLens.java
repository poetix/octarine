package com.codepoetics.octarine.functional.lenses;

import com.codepoetics.octarine.functional.extractors.Extractor;
import com.codepoetics.octarine.functional.functions.Partial;
import org.pcollections.PMap;
import org.pcollections.PVector;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public interface OptionalLens<T, V> extends Lens<T, Optional<V>>, Extractor.FromPartial<T, V> {

    static <T, V> OptionalLens<T, V> of(Function<T, Optional<V>> getter, BiFunction<T, Optional<V>, T> setter) {
        return new OptionalLens<T, V>() {
            @Override
            public Optional<V> get(T instance) {
                return getter.apply(instance);
            }

            @Override
            public T set(T instance, Optional<V> newValue) {
                return setter.apply(instance, newValue);
            }
        };
    }

    static <T, V> OptionalLens<T, V> wrap(Lens<T, Optional<V>> lens) {
        return new OptionalLens<T, V>() {
            @Override
            public Optional<V> get(T instance) {
                return lens.get(instance);
            }

            @Override
            public T set(T instance, Optional<V> newValue) {
                return lens.set(instance, newValue);
            }
        };
    }

    static <T> OptionalLens<T[], T> intoArray(int index) {
        return of(
                (T[] ts) -> Optional.ofNullable(ts[index]),
                (T[] ts, Optional<T> t) -> {
                    T[] copy = Arrays.copyOf(ts, ts.length);
                    copy[index] = t.orElse(null);
                    return copy;
                }
        );
    }

    static <K, V> OptionalLens<PMap<K, V>, V> intoPMap(K key) {
        return of(
                (PMap<K, V> m) -> Optional.ofNullable(m.get(key)),
                (PMap<K, V> m, Optional<V> v) -> v.isPresent() ? m.plus(key, v.get()) : m.minus(key)
        );
    }

    static <T> OptionalLens<PVector<T>, T> intoPVector(int index) {
        return of(
                ts -> index < ts.size() ? Optional.ofNullable(ts.get(index)) : Optional.empty(),
                (PVector<T> ts, Optional<T> t) -> t.isPresent() ? ts.with(index, t.get()) : ts.with(index, null)
        );
    }

    default Optional<V> apply(T target) {
        return get(target);
    }

    default T updateIfPresent(T target, UnaryOperator<V> updater) {
        return set(target, get(target).map(updater));
    }

    default T updateIfPresent(T target, Partial<V, V> partialUpdater) {
        return set(target, get(target).flatMap(partialUpdater));
    }

    default V orElse(T target, V defaultValue) {
        return get(target).orElse(defaultValue);
    }

    default T setNullable(T target, V newValue) {
        return set(target, Optional.ofNullable(newValue));
    }

    default <V2> OptionalLens<T, V2> join(OptionalLens<V, V2> next, Supplier<V> missingValueSupplier) {
        return of(
                (T t) -> get(t).flatMap(next::get),
                (T t, Optional<V2> v2) -> set(t, Optional.of(next.set(get(t).orElseGet(missingValueSupplier), v2))));

    }

    default Lens<T, V> assertPresent() {
        return Lens.of(t -> get(t).get(), (t, v) -> set(t, Optional.ofNullable(v)));
    }

    default Lens<T, V> withDefault(V defaultValue) {
        return Lens.of(
                t -> get(t).orElse(defaultValue),
                (t, v) -> set(t, Optional.ofNullable(v)));
    }

    default Lens<T, V> withDefault(Supplier<V> defaultValue) {
        return Lens.of(t -> get(t).orElseGet(defaultValue), (t, v) -> set(t, Optional.ofNullable(v)));
    }
}
