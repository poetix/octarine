package com.codepoetics.octarine.tuples;

public final class Tuple {
    private Tuple() {
    }

    public static T0 empty() {
        return T0.instance;
    }

    public static <A> T1<A> of(A a) {
        return T1.of(a);
    }

    public static <A, B> T2<A, B> of(A a, B b) {
        return T2.of(a, b);
    }

    public static <A, B, C> T3<A, B, C> of(A a, B b, C c) {
        return T3.of(a, b, c);
    }

    public static <A, B, C, D> T4<A, B, C, D> of(A a, B b, C c, D d) {
        return T4.of(a, b, c, d);
    }

    public static <A, B, C, D, E> T5<A, B, C, D, E> of(A a, B b, C c, D d, E e) {
        return T5.of(a, b, c, d, e);
    }

}
