package com.codepoetics.octarine.functional.functions;

import java.util.function.Function;

public interface F3<A, B, C, R> {
    R apply(A a, B b, C c);

    default F2<B, C, R> curry(A a) {
        return (b, c) -> apply(a, b, c);
    }

    default F1<C, R> curry(A a, B b) {
        return c -> apply(a, b, c);
    }

    default F0<R> curry(A a, B b, C c) {
        return () -> apply(a, b, c);
    }

    default <R2> F3<A, B, C, R2> andThen(Function<R, R2> f) {
        return (a, b, c) -> f.apply(apply(a, b, c));
    }

}
