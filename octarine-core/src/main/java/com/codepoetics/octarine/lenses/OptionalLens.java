package com.codepoetics.octarine.lenses;

import org.pcollections.PMap;
import org.pcollections.PVector;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Supplier;

public interface OptionalLens<T, V> extends Lens<T, Optional<V>> {

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

    default V getOrElse(T target, V defaultValue) { return get(target).orElse(defaultValue); }
    default T setNullable(T target, V newValue) { return set(target, Optional.ofNullable(newValue)); }
    default <V2> OptionalLens<T, V2> thenMaybe(OptionalLens<V, V2> next, Supplier<V> missingValueSupplier) {
        OptionalLens<T, V> self = this;
        return new OptionalLens<T, V2>() {

            @Override
            public Optional<V2> get(T instance) {
                Optional<V> v = self.get(instance);
                if (v.isPresent()) {
                    return next.get(v.get());
                }
                return Optional.empty();
            }

            @Override
            public T set(T instance, Optional<V2> newValue) {
                Optional<V> maybeV = self.get(instance);
                if (!maybeV.isPresent()) {
                    return newValue.isPresent()
                            ? self.setNullable(instance, next.set(missingValueSupplier.get(), newValue))
                            : instance;
                }
                return self.setNullable(instance, next.set(maybeV.get(), newValue));
            }

        };
    }

    default Lens<T, V> withDefault(Supplier<V> defaultValue) {
        return Lens.of(t -> get(t).orElseGet(defaultValue), (t, v) -> set(t, Optional.ofNullable(v)));
    }

    static <T> OptionalLens<T[], T> intoArray(int index) {
        return wrap(Lens.of(
                (T[] ts) -> index < ts.length ? Optional.ofNullable(ts[index]) : Optional.empty(),
                (ts, t) -> {
                    T[] copy = Arrays.copyOf(ts, ts.length);
                    copy[index] = t.orElse(null);
                    return copy;
                }
        ));
    }

    static <K, V> OptionalLens<PMap<K, V>, V> intoPMap(K key) {
        return wrap(Lens.of(
                (PMap<K, V> m) -> Optional.ofNullable(m.get(key)),
                (PMap<K, V> m, Optional<V> v) -> v.isPresent() ? m.plus(key, v.get()) : m.minus(key)
        ));
    }

    static <T> OptionalLens<PVector<T>, T> intoPVector(int index) {
        return wrap(Lens.of(
                ts -> index < ts.size() ? Optional.ofNullable(ts.get(index)) : Optional.empty(),
                (PVector<T> ts, Optional<T> t) -> t.isPresent() ? ts.with(index, t.get()) : ts.with(index, null)
        ));
    }
}
