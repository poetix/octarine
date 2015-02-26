package com.codepoetics.octarine.functional.functions;

import java.util.function.BiFunction;

public interface F2<A, B, R> extends BiFunction<A, B, R> {

    static <A, B, R> F2<A, B, R> of(BiFunction<? super A, ? super B, ? extends R> f) {
        return f::apply;
    }
    static <A, B, R> F1<A, R> of(BiFunction<? super A, ? super B, ? extends R> f, B b) {
        return a -> f.apply(a, b);
    }

    default F1<B, R> curry(A a) {
        return b -> apply(a, b);
    }

    default F1<A, R> withParams(B b) {
        return a -> apply(a, b);
    }

    default F0<R> curry(A a, B b) {
        return () -> apply(a, b);
    }
}
