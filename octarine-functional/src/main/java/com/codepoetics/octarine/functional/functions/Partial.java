package com.codepoetics.octarine.functional.functions;

import java.util.Optional;
import java.util.function.Function;

public interface Partial<I, O> extends F1<I, Optional<O>> {

    static <I, O> Partial<I, O> of(Function<I, Optional<O>> f) {
        return f::apply;
    }

    default <O2> Partial<I, O2> bind(Function<? super O, ? extends O2> next) {
        return i -> apply(i).map(next);
    }

    default <O2> Partial<I, O2> bind(Partial<? super O, O2> next) {
        return i -> apply(i).flatMap(next);
    }

}
