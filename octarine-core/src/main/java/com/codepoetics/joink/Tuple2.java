package com.codepoetics.joink;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

public class Tuple2<T0, T1> {

    public static <T0, T1> Tuple2<T0, T1> of(T0 first, T1 second) {
        return new Tuple2<T0, T1>(first, second);
    }

    private final T0 first;
    private final T1 second;

    private Tuple2(T0 first, T1 second) {
        this.first = first;
        this.second = second;
    }

    public T0 first() {
        return first;
    }

    public T1 second() {
        return second;
    }

    @Override
    public boolean equals(Object other) {
        if (other == null || !(other instanceof Tuple2)) { return false; }
        Tuple2<Object, Object> otherTuple = (Tuple2<Object, Object>) other;
        return Objects.equals(first, otherTuple.first) &&
                Objects.equals(second, otherTuple.second);
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, second);
    }

    @Override
    public String toString() {
        return String.format("(%s, %s)", first, second);
    }

    public Tuple2<T1, T0> swap() { return Tuple2.of(second(), first()); }

    public static <T0, T1, R> Function<Tuple2<T0, T1>, R> mergingWith(BiFunction<T0, T1, R> bf) {
        return t -> bf.apply(t.first, t.second);
    }
}
