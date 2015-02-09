package com.codepoetics.octarine.functional.tuples;

import com.codepoetics.octarine.functional.lenses.Lens;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

public final class T1<A> implements Supplier<A> {

    private final A a;

    private T1(A a) {
        this.a = a;
    }

    public static <A> Lens<T1<A>, A> first() {
        return Lens.of(
                T1::getFirst,
                T1::withFirst
        );
    }

    public static <A> T1<A> of(A a) {
        return new T1<>(a);
    }

    public <R> R sendTo(Function<A, R> f) {
        return f.apply(a);
    }

    @Override
    public A get() {
        return a;
    }

    public A getFirst() {
        return a;
    }

    public <A2> T1<A2> withFirst(A2 a2) {
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
        if (!(o instanceof T1)) {
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
