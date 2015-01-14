package com.codepoetics.octarine.functional.functions;

import java.util.Optional;

public interface Partial<I, O> extends F1<I, Optional<O>> {

    default <O2> Partial<I, O2> bind(Partial<O, O2> next) {
        return i -> apply(i).flatMap(next);
    }

}
