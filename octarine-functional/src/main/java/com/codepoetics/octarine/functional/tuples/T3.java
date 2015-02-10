package com.codepoetics.octarine.functional.tuples;

import com.codepoetics.octarine.functional.functions.F3;

import java.util.Objects;
import java.util.function.Function;

public final class T3<A, B, C> {
    private final A a;
    private final B b;
    private final C c;

    private T3(A a, B b, C c) {
        this.a = a;
        this.b = b;
        this.c = c;
    }

    public static <S, A, B, C> Function<S, T3<A, B, C>> unpacker(Function<? super S, ? extends A> first,
                                                                 Function<? super S, ? extends B> second,
                                                                 Function<? super S, ? extends C> third) {
        return s -> T3.of(first.apply(s), second.apply(s), third.apply(s));
    }

    public static <A, B, C> TupleLens<T3<A, B, C>, A> first() {
        return TupleLens.of(
                0,
                T3::getFirst,
                T3::withFirst
        );
    }

    public static <A, B, C> TupleLens<T3<A, B, C>, B> second() {
        return TupleLens.of(
                1,
                T3::getSecond,
                T3::withSecond
        );
    }

    public static <A, B, C> TupleLens<T3<A, B, C>, C> third() {
        return TupleLens.of(
                2,
                T3::getThird,
                T3::withThird
        );
    }

    public static <A, B, C> T3<A, B, C> of(A a, B b, C c) {
        return new T3<>(a, b, c);
    }

    public A getFirst() {
        return a;
    }

    public <A2> T3<A2, B, C> withFirst(A2 a2) {
        return T3.of(a2, b, c);
    }

    public B getSecond() {
        return b;
    }

    public <B2> T3<A, B2, C> withSecond(B2 b2) {
        return T3.of(a, b2, c);
    }

    public C getThird() {
        return c;
    }

    public <C2> T3<A, B, C2> withThird(C2 c2) {
        return T3.of(a, b, c2);
    }

    public <R> R pack(F3<? super A, ? super B, ? super C, ? extends R> f) {
        return f.apply(a, b, c);
    }

    public <D> T4<A, B, C, D> push(D d) {
        return Tuple.of(a, b, c, d);
    }

    public T2<C, T2<A, B>> pop() {
        return Tuple.of(c, Tuple.of(a, b));
    }

    public T2<A, T2<B, C>> shift() {
        return Tuple.of(a, Tuple.of(b, c));
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof T3)) {
            return false;
        }
        T3 other = (T3) o;
        return Objects.equals(a, other.a) &&
                Objects.equals(b, other.b) &&
                Objects.equals(c, other.c);
    }

    @Override
    public int hashCode() {
        return Objects.hash(a, b, c);
    }

    @Override
    public String toString() {
        return String.format("(%s, %s, %s)", a, b, c);
    }
}
