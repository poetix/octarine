package com.codepoetics.octarine.records;

import java.util.function.Function;

public interface NamedFunction<T, V> extends Function<T, V> {

    static <T, V> NamedFunction<T, V> of(Function<T, V> f, String name) {
        return new NamedFunction<T, V>() {
            @Override
            public String name() {
                return name;
            }

            @Override
            public V apply(T t) {
                return f.apply(t);
            }
        };
    }

    String name();
}
