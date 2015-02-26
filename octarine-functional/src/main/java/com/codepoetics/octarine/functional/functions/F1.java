package com.codepoetics.octarine.functional.functions;

import java.util.function.Function;

public interface F1<A, R> extends Function<A, R> {

    static <A, R> F1<A, R> of(Function<? super A, ? extends R> f) {
        return f::apply;
    }

    static <A, R> F1<A, R> of(Unsafe<? super A, ? extends R> f) {
        return f::apply;
    }

    default F0<R> curry(A a) {
        return () -> apply(a);
    }

    default F0<R> withParams(A a) {
        return curry(a);
    }

    static interface Unsafe<A, R> extends F1<A, R> {
        default R apply(A a) {
            try {
                return applyUnsafe(a);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        R applyUnsafe(A a) throws Exception;
    }
}
