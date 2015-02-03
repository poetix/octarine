package com.codepoetics.octarine.functional.consumers;

public interface C3<A, B, C> {
    void accept(A a, B b, C c);

    default C3<A, B, C> andThen(C3<A, B, C> next) {
        return (a, b, c) -> {
            accept(a, b, c);
            next.accept(a, b, c);
        };
    }
}
