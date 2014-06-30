package com.codepoetics.octarine.tuples;

import com.codepoetics.octarine.lenses.Lens;

import java.util.Objects;
import java.util.function.BiFunction;

public final class T2<A, B> {

    private final A a;
    private final B b;

    private T2(A a, B b) {
        this.a = a;
        this.b = b;
    }

    public static <A, B> Lens<T2<A, B>, A> __1() {
        return Lens.of(
                T2::_1,
                T2::_1
        );
    }

    public static <A, B> Lens<T2<A, B>, B> __2() {
        return Lens.of(
                T2::_2,
                T2::_2
        );
    }

    public static <A, B> T2<A, B> of(A a, B b) {
        return new T2<A, B>(a, b);
    }

    public A _1() {
        return a;
    }

    public <A2> T2<A2, B> _1(A2 a2) {
        return T2.of(a2, b);
    }

    public B _2() {
        return b;
    }

    public <B2> T2<A, B2> _2(B2 b2) {
        return T2.of(a, b2);
    }

    public <R> R sendTo(BiFunction<A, B, R> f) {
        return f.apply(a, b);
    }

    public <C> T3<A, B, C> push(C c) {
        return Tuple.of(a, b, c);
    }

    public T2<B, T1<A>> pop() {
        return Tuple.of(b, Tuple.of(a));
    }

    public T2<A, T1<B>> shift() {
        return Tuple.of(a, Tuple.of(b));
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof T2)) {
            return false;
        }
        T2 other = (T2) o;
        return Objects.equals(a, other.a) &&
                Objects.equals(b, other.b);
    }

    @Override
    public int hashCode() {
        return Objects.hash(a, b);
    }

    @Override
    public String toString() {
        return String.format("(%s, %s)", a, b);
    }
}
