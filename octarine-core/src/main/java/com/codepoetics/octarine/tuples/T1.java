package com.codepoetics.octarine.tuples;

import com.codepoetics.octarine.lenses.Lens;

import java.util.Objects;
import java.util.function.Function;

public final class T1<A> {

    private final A a;

    private T1(A a) {
        this.a = a;
    }

    public static <A> Lens<T1<A>, A> __1() {
        return Lens.of(
                T1::_1,
                T1::_1
        );
    }

    public static <A> T1<A> of(A a) {
        return new T1<A>(a);
    }

    public <R> R sendTo(Function<A, R> f) {
        return f.apply(a);
    }

    public A _1() {
        return a;
    }

    public <A2> T1<A2> _1(A2 a2) {
        return T1.of(a2);
    }

    public T2<A, T0> pop() {
        return Tuple.of(a, T0.instance);
    }

    public T2<A, T0> shift() {
        return Tuple.of(a, T0.instance);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof T1)) {
            return false;
        }
        T1 other = (T1) o;
        return Objects.equals(a, other.a);
    }

    @Override
    public int hashCode() {
        return a.hashCode();
    }

    @Override
    public String toString() {
        return String.format("(%s)", a);
    }
}
