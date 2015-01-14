package com.codepoetics.octarine.functional.lenses;

import com.codepoetics.octarine.functional.functions.Partial;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public interface OptionalFocus<T, V> extends Focus<T, Optional<V>> {

    static <T, V> OptionalFocus<T, V> of(Supplier<Optional<V>> project, Function<Optional<V>, T> inject) {
        return new OptionalFocus<T, V>() {
            @Override
            public T apply(Optional<V> v) {
                return inject.apply(v);
            }

            @Override
            public Optional<V> get() {
                return project.get();
            }
        };
    }

    static <T, V> OptionalFocus<T, V> with(T target, Function<T, Optional<V>> getter, BiFunction<T, Optional<V>, T> setter) {
        return of(() -> getter.apply(target), v -> setter.apply(target, v));
    }

    static <T, V> OptionalFocus<T, V> from(Focus<T, Optional<V>> focus) {
        return of(focus::get, focus::apply);
    }

    default V orElse(V defaultValue) {
        return get().orElse(defaultValue);
    }

    default V orElseGet(Supplier<V> defaultValueSupplier) {
        return get().orElseGet(defaultValueSupplier);
    }

    default T updateIfPresent(UnaryOperator<V> updater) {
        return apply(get().map(updater));
    }

    default T updateIfPresent(Partial<V, V> partialUpdater) {
        return apply(get().flatMap(partialUpdater));
    }

    default <V2> Optional<V2> map(Function<V, V2> f) {
        return get().map(f);
    }

    default <V2> Optional<V2> flatMap(Function<V, Optional<V2>> f) {
        return get().flatMap(f);
    }

}
