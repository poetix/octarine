package com.codepoetics.octarine.functions;

import java.util.function.Function;

public interface F1<A, R> extends Function<A, R> {
    default F0<R> curry(A a) {
        return () -> apply(a);
    }
}
