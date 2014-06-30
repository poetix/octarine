package com.codepoetics.octarine.consumers;

public interface C3<A, B, C> {
    void accept(A a, B b, C c);
}
