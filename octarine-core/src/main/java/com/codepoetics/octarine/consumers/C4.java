package com.codepoetics.octarine.consumers;

public interface C4<A, B, C, D> {
    void accept(A a, B b, C c, D d);
}
