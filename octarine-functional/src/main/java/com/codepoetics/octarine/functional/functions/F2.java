package com.codepoetics.octarine.functional.functions;

import java.util.function.BiFunction;

public interface F2<A, B, R> extends BiFunction<A, B, R> {
    default F1<B, R> curry(A a) {
        return b -> apply(a, b);
    }

    default F0<R> curry(A a, B b) {
        return () -> apply(a, b);
    }
}
