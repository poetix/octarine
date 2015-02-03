package com.codepoetics.octarine.functional.consumers;

public interface C4<A, B, C, D> {
    void accept(A a, B b, C c, D d);

    default C4<A, B, C, D> andThen(C4<A, B, C, D> next) {
        return (a, b, c, d) -> {
            accept(a, b, c, d);
            next.accept(a, b, c, d);
        };
    }
}
