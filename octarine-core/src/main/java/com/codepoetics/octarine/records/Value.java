package com.codepoetics.octarine.records;

import com.codepoetics.octarine.com.codepoetics.octarine.tuples.Pair;
import com.codepoetics.octarine.com.codepoetics.octarine.tuples.Pairable;

public interface Value extends Pairable<Key<?>, Object> {

    Key<?> key();
    Object value();

    default Pair<Key<?>, Object> toPair() { return Pair.of(key(), value()); }
}
