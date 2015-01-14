package com.codepoetics.octarine.functional.lenses;

import java.util.Optional;
import java.util.function.Function;

public interface LensLike<T, V, F extends Focus<T, V>> {

    F on(T instance);

    default V get(T instance) {
        return on(instance).get();
    }

    default T set(T instance, V newValue) {
        return on(instance).apply(newValue);
    }

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

    default <V2> Lens<T, V2> join(LensLike<V, V2, ? extends Focus<V, V2>> next) {
        return Lens.of(
                t -> next.get(get(t)),
                (t, v) -> set(t, next.set(get(t), v))
        );
    }

    default <V2> OptionalLens<T, V2> join(OptionalLens<V, V2> next) {
        return OptionalLens.wrap(Lens.of(
                t -> next.get(get(t)),
                (t, v) -> set(t, next.set(get(t), v))
        ));
    }
}
