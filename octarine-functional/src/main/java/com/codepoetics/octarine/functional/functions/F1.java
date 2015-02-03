package com.codepoetics.octarine.functional.functions;

import java.util.function.Function;

public interface F1<A, R> extends Function<A, R> {

    static <A, R> F1<A, R> of(Function<A, R> f) {
        return f::apply;
    }

    default F0<R> curry(A a) {
        return () -> apply(a);
    }
}
