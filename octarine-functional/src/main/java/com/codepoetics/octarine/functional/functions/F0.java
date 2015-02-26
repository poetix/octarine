package com.codepoetics.octarine.functional.functions;

import java.util.function.Function;
import java.util.function.Supplier;

public interface F0<R> extends Supplier<R> {
    static <R> F0<R> of(Supplier<R> supplier) {
        return supplier::get;
    }

    static <R> F0<R> unsafe(Unsafe<R> supplier) {
        return supplier::get;
    }

    default <R2> F0<R2> andThen(Function<? super R, ? extends R2> next) {
        return () -> next.apply(get());
    }

    static interface Unsafe<R> extends F0<R> {
        default R get() {
            try {
                return unsafeGet();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        R unsafeGet() throws Exception;
    }
}
