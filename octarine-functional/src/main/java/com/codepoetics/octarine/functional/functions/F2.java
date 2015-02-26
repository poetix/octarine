package com.codepoetics.octarine.functional.functions;

import java.util.function.BiFunction;

public interface F2<A, B, R> extends BiFunction<A, B, R> {

    static <A, B, R> F2<A, B, R> of(BiFunction<? super A, ? super B, ? extends R> f) {
        return f::apply;
    }
    static <A, B, R> F1<A, R> of(BiFunction<? super A, ? super B, ? extends R> f, B b) {
        return a -> f.apply(a, b);
    }

    static <A, B, R> F2<A, B, R> unsafe(Unsafe<? super A, ? super B, ? extends R> f) {
        return f::apply;
    }
    static <A, B, R> F1<A, R> unsafe(Unsafe<? super A, ? super B, ? extends R> f, B b) {
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

    static interface Unsafe<A, B, R> extends F2<A, B, R> {
        default R apply(A a, B b) {
            try {
                return applyUnsafe(a, b);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        R applyUnsafe(A a, B b) throws Exception;
    }
}
