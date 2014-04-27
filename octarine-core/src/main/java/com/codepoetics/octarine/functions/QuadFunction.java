package com.codepoetics.octarine.functions;

import java.util.function.BiFunction;
import java.util.function.Function;

public interface QuadFunction<A, B, C, D, R> {
    R apply(A a, B b, C c, D d);
    default TriFunction<B, C, D, R> curry(A a) {
        return (b, c, d) -> this.apply(a, b, c, d);
    }
    default BiFunction<C, D, R> curry(A a, B b) {
        return (c, d) -> this.apply(a, b, c, d);
    }
    default Function<D, R> curry(A a, B b, C c) {
        return d -> this.apply(a, b, c, d);
    }
    default <R2> QuadFunction<A, B, C, D, R2> andThen(Function<? super R, ? extends R2> next) {
        return (a, b, c, d) -> next.apply(this.apply(a, b, c, d));
    }
}
