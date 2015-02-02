package com.codepoetics.octarine.functional.lenses;

import java.util.Optional;
import java.util.function.Function;

interface LensLike<T, V, F extends Focus<T, V>> {

    F into(T instance);

    V get(T instance);

    T set(T instance, V newValue);

    default OptionalLens<T, V> asOptional() {
        return OptionalLens.wrap(Lens.<T, Optional<V>>of(
                t -> Optional.ofNullable(get(t)),
                (t, v) -> set(t, v.orElse(null))
        ));
    }

    default T update(T instance, Function<V, V> updater) {
        return set(instance, updater.apply(get(instance)));
    }

    default Function<T, T> inject(V newValue) {
        return t -> set(t, newValue);
    }

    default Function<T, T> inflect(Function<V, V> updater) {
        return t -> update(t, updater);
    }

    default <V2> Lens<T, V2> join(Lens<V, V2> next) {
        return Lens.of(
                t -> next.get(get(t)),
                (t, v) -> set(t, next.set(get(t), v))
        );
    }

    default <V2> OptionalLens<T, V2> join(OptionalLens<V, V2> next) {
        return OptionalLens.of(
                t -> next.get(get(t)),
                (t, v) -> set(t, next.set(get(t), v))
        );
    }
}
