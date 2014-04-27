package com.codepoetics.octarine.functions;

import java.util.function.BiFunction;
import java.util.function.Function;

public interface TriFunction<A, B, C, R> {
    R apply(A a, B b, C c);
    default BiFunction<B, C, R> curry(A a) {
        return (b, c) -> this.apply(a, b, c);
    }
    default Function<C, R> curry(A a, B b) {
        return c -> this.apply(a, b, c);
    }
    default <R2> TriFunction<A, B, C, R2> andThen(Function<? super R, ? extends R2> next) {
        return (a, b, c) -> next.apply(this.apply(a, b, c));
    }
}
