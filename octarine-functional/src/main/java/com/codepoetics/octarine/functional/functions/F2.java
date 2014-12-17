package com.codepoetics.octarine.functional.functions;

import com.codepoetics.octarine.functional.tuples.T2;

import java.util.function.BiFunction;
import java.util.function.Function;

public interface F2<A, B, R> extends BiFunction<A, B, R> {
    default F1<B, R> curry(A a) {
        return b -> apply(a, b);
    }

    default F0<R> curry(A a, B b) {
        return () -> apply(a, b);
    }

    default <S> Function<S, R> compose(Function<S, T2<A, B>> f) {
        return s -> f.apply(s).sendTo(this);
    }
}
