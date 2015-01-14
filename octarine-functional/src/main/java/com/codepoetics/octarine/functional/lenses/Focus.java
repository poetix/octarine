package com.codepoetics.octarine.functional.lenses;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public interface Focus<T, V> extends Supplier<V>, Function<V, T> {

    static <T, V> Focus<T, V> of(Supplier<V> project, Function<V, T> inject) {
        return new Focus<T, V>() {
            @Override
            public T apply(V newValue) {
                return inject.apply(newValue);
            }

            @Override
            public V get() {
                return project.get();
            }
        };
    }

    static <T, V> Focus<T, V> with(T target, Function<T, V> getter, BiFunction<T, V, T> setter) {
        return of(() -> getter.apply(target), v -> setter.apply(target, v));
    }

    default T update(UnaryOperator<V> updater) {
        return apply(updater.apply(get()));
    }

}
