package com.codepoetics.octarine.functional.functions;

import com.codepoetics.octarine.functional.tuples.T5;

import java.util.function.Function;

public interface F5<A, B, C, D, E, R> {
    R apply(A a, B b, C c, D d, E e);

    default F4<B, C, D, E, R> curry(A a) {
        return (b, c, d, e) -> apply(a, b, c, d, e);
    }

    default F3<C, D, E, R> curry(A a, B b) {
        return (c, d, e) -> apply(a, b, c, d, e);
    }

    default F2<D, E, R> curry(A a, B b, C c) {
        return (d, e) -> apply(a, b, c, d, e);
    }

    default F1<E, R> curry(A a, B b, C c, D d) {
        return e -> apply(a, b, c, d, e);
    }

    default <R2> F5<A, B, C, D, E, R2> andThen(Function<R, R2> f) {
        return (a, b, c, d, e) -> f.apply(apply(a, b, c, d, e));
    }

    default <S> Function<S, R> compose(Function<S, T5<A, B, C, D, E>> f) {
        return s -> f.apply(s).sendTo(this);
    }
}
