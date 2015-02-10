package com.codepoetics.octarine.functional.tuples;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

public final class T1<A> implements Supplier<A> {

    private final A a;

    private T1(A a) {
        this.a = a;
    }

    public static <S, A> Function<S, T1<A>> unpacker(Function<? super S, ? extends A> first) {
        return s -> T1.of(first.apply(s));
    }

    public static <A> TupleLens<T1<A>, A> first() {
        return TupleLens.of(
                0,
                T1::getFirst,
                T1::withFirst
        );
    }

    public static <A> T1<A> of(A a) {
        return new T1<>(a);
    }

    public <R> R pack(Function<? super A, ? extends R> f) {
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
        return Tuple.of(a, T0.INSTANCE);
    }

    public T2<A, T0> shift() {
        return Tuple.of(a, T0.INSTANCE);
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
