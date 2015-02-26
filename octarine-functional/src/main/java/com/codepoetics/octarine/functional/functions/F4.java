package com.codepoetics.octarine.functional.functions;

import java.util.function.Function;

public interface F4<A, B, C, D, R> {

    static <A, B, C, D, R> F4<A, B, C, D, R> of(F4<A, B, C, D, R> f) {
        return f;
    }

    static <A, B, C, D, R> F3<A, C, D, R> of(F4<A, B, C, D, R> f, B b) {
        return (a, c, d) -> f.apply(a, b, c, d);
    }

    static <A, B, C, D, R> F2<A, D, R> of(F4<A, B, C, D, R> f, B b, C c) {
        return (a, d) -> f.apply(a, b, c, d);
    }

    static <A, B, C, D, R> F1<A, R> of(F4<A, B, C, D, R> f, B b, C c, D d) {
        return a -> f.apply(a, b, c, d);
    }

    R apply(A a, B b, C c, D d);

    default F3<B, C, D, R> curry(A a) {
        return (b, c, d) -> apply(a, b, c, d);
    }

    default F2<C, D, R> curry(A a, B b) {
        return (c, d) -> apply(a, b, c, d);
    }

    default F1<D, R> curry(A a, B b, C c) {
        return d -> apply(a, b, c, d);
    }

    default F0<R> curry(A a, B b, C c, D d) {
        return () -> apply(a, b, c, d);
    }

    default F1<A, R> withParams(B b, C c, D d) {
        return a -> apply(a, b, c, d);
    }

    default <R2> F4<A, B, C, D, R2> andThen(Function<? super R, ? extends R2> f) {
        return (a, b, c, d) -> f.apply(apply(a, b, c, d));
    }
}
