package com.codepoetics.octarine.functional.functions;

import java.util.function.Function;

public interface F5<A, B, C, D, E, R> {

    static <A, B, C, D, E, R> F5<A, B, C, D, E, R> of(F5<A, B, C, D, E, R> f) {
        return f;
    }

    static <A, B, C, D, E, R> F4<A, C, D, E, R> of(F5<A, B, C, D, E, R> f, B b) {
        return (a, c, d, e) -> f.apply(a, b, c, d, e);
    }

    static <A, B, C, D, E, R> F3<A, D, E, R> of(F5<A, B, C, D, E, R> f, B b, C c) {
        return (a, d, e) -> f.apply(a, b, c, d, e);
    }

    static <A, B, C, D, E, R> F2<A, E, R> of(F5<A, B, C, D, E, R> f, B b, C c, D d) {
        return (a, e) -> f.apply(a, b, c, d, e);
    }

    static <A, B, C, D, E, R> F1<A, R> of(F5<A, B, C, D, E, R> f, B b, C c, D d, E e) {
        return a -> f.apply(a, b, c, d, e);
    }

    static <A, B, C, D, E, R> F5<A, B, C, D, E, R> unsafe(Unsafe<A, B, C, D, E, R> f) {
        return f;
    }

    static <A, B, C, D, E, R> F4<A, C, D, E, R> unsafe(Unsafe<A, B, C, D, E, R> f, B b) {
        return (a, c, d, e) -> f.apply(a, b, c, d, e);
    }

    static <A, B, C, D, E, R> F3<A, D, E, R> unsafe(Unsafe<A, B, C, D, E, R> f, B b, C c) {
        return (a, d, e) -> f.apply(a, b, c, d, e);
    }

    static <A, B, C, D, E, R> F2<A, E, R> unsafe(Unsafe<A, B, C, D, E, R> f, B b, C c, D d) {
        return (a, e) -> f.apply(a, b, c, d, e);
    }

    static <A, B, C, D, E, R> F1<A, R> unsafe(Unsafe<A, B, C, D, E, R> f, B b, C c, D d, E e) {
        return a -> f.apply(a, b, c, d, e);
    }

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

    default F1<A, R> withParams(B b, C c, D d, E e) {
        return a -> apply(a, b, c, d, e);
    }

    default <R2> F5<A, B, C, D, E, R2> andThen(Function<? super R, ? extends R2> f) {
        return (a, b, c, d, e) -> f.apply(apply(a, b, c, d, e));
    }


    static interface Unsafe<A, B, C, D, E, R> extends F5<A, B, C, D, E, R> {
        default R apply(A a, B b, C c, D d, E e) {
            try {
                return applyUnsafe(a, b, c, d, e);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }

        R applyUnsafe(A a, B b, C c, D d, E e) throws Exception;
    }
}
