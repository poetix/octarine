package com.codepoetics.octarine.lenses;

import java.util.Optional;
import java.util.function.Function;

public interface Chainable<T, V> extends Function<T, Optional<V>> {
    default <V2> Chainable<T, V2> chain(Function<? super V, Optional<V2>> f) {
        return t -> this.apply(t).flatMap(f);
    }
}
