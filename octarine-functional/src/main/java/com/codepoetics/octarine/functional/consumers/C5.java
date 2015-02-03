package com.codepoetics.octarine.functional.consumers;

public interface C5<A, B, C, D, E> {
    void accept(A a, B b, C c, D d, E e);

    default C5<A, B, C, D, E> andThen(C5<A, B, C, D, E> next) {
        return (a, b, c, d, e) -> {
            accept(a, b, c, d, e);
            next.accept(a, b, c, d, e);
        };
    }
}
