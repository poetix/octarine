package com.codepoetics.octarine.io;

import java.io.IOException;
import java.util.function.Function;

public interface IO<T> {
    static <T> IO<T> unit(T value) { return () -> value; }
    static <A, B, C> Function<A, IO<C>> bind(Function<A, IO<B>> f1, Function<B, IO<C>> f2) {
        return a -> () -> f2.apply(f1.apply(a).run()).run();
    }
    T run() throws IOException;
}
