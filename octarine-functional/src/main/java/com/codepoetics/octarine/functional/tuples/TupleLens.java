package com.codepoetics.octarine.functional.tuples;

import com.codepoetics.octarine.functional.lenses.Lens;
import com.codepoetics.octarine.functional.paths.Path;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

public interface TupleLens<T, V> extends Lens<T, V>, Path<T, V> {
    static <T, V> TupleLens<T, V> of(int index, Function<? super T, ? extends V> getter, BiFunction<? super T, ? super V, ? extends T> setter) {
        return new TupleLens<T, V>() {
            @Override
            public V get(T instance) {
                return getter.apply(instance);
            }

            @Override
            public T set(T instance, V newValue) {
                return setter.apply(instance, newValue);
            }

            @Override
            public void describe(StringBuilder sb) {
                sb.append("[").append(index).append("]");
            }
        };
    }

    default Optional<V> apply(T t) {
        return Optional.of(get(t));
    }
}
