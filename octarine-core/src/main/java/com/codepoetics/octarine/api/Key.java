package com.codepoetics.octarine.api;

import com.codepoetics.octarine.functional.lenses.OptionalFocus;
import com.codepoetics.octarine.functional.lenses.OptionalLens;
import com.codepoetics.octarine.functional.paths.Path;

public interface Key<T> extends OptionalLens<Record, T>, Path.Named<Record, T> {

    @Override
    default public boolean test(Record instance) {
        return instance.containsKey(this);
    }

    @Override
    default public OptionalFocus<Record, T> on(Record record) {
        return OptionalFocus.with(
                record,
                r -> r.get(this),
                (r, nv) -> nv.map(v -> r.with(of(v))).orElseGet(() -> r.without(this)));
    }

    String name();

    Record metadata();

    Value of(T value);

}
