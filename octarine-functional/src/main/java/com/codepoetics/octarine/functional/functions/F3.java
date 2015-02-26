package com.codepoetics.octarine.functional.functions;

import java.util.function.Function;

public interface F3<A, B, C, R> {

    static <A, B, C, R> F3<A, B, C, R> of(F3<A, B, C, R> f) {
        return f;
    }

    static <A, B, C, R> F2<A, C, R> of(F3<A, B, C, R> f, B b) {
        return (a, c) -> f.apply(a, b, c);
    }

    static <A, B, C, R> F1<A, R> of(F3<A, B, C, R> f, B b, C c) {
        return a -> f.apply(a, b, c);
    }

    static <A, B, C, R> F3<A, B, C, R> unsafe(Unsafe<A, B, C, R> f) {
        return f;
    }

    static <A, B, C, R> F2<A, C, R> unsafe(Unsafe<A, B, C, R> f, B b) {
        return (a, c) -> f.apply(a, b, c);
    }

    static <A, B, C, R> F1<A, R> unsafe(Unsafe<A, B, C, R> f, B b, C c) {
        return a -> f.apply(a, b, c);
    }

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

    default F1<A, R> withParams(B b, C c) {
        return a -> apply(a, b, c);
    }

    default <R2> F3<A, B, C, R2> andThen(Function<? super R, ? extends R2> f) {
        return (a, b, c) -> f.apply(apply(a, b, c));
    }

    static interface Unsafe<A, B, C, R> extends F3<A, B, C, R> {
        default R apply(A a, B b, C c) {
            try {
                return applyUnsafe(a, b, c);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        R applyUnsafe(A a, B b, C c) throws Exception;
    }

}
