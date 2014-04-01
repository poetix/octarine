package com.codepoetics.octarine.tuples;

import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

public interface Pair<K, V> extends Map.Entry<K, V>, Pairable<K, V> {

    public static <K, V> Pair<K, V> of(K first, V second) {
        return new Pair<K, V>() {

            @Override public K first() { return first; }
            @Override public V second() { return second; }

            @Override public Pair<V, K> swap() {
                Pair<K, V> original = this;
                return new Pair<V, K>() {

                    @Override public V first() { return second; }
                    @Override public K second() { return first; }

                    @Override public Pair<K, V> swap() { return original; }
                };
            }
        };
    }

    public static <K, V> Pair<K, V> from(Map.Entry<K, V> entry) { return of(entry.getKey(), entry.getValue()); }

    public static <K, V, K2> Function<Pair<K, V>, Pair<K2, V>> mapFirst(Function<K, K2> f) {
        return p -> Pair.of(f.apply(p.first()), p.second());
    }

    public static <K, V, V2> Function<Pair<K, V>, Pair<K, V2>> mapSecond(Function<V, V2> f) {
        return p -> Pair.of(p.first(), f.apply(p.second()));
    }

    public static <K, V, T> Function<Pair<K, V>, T> reduce(BiFunction<K, V, T> bf) {
        return p -> bf.apply(p.first(), p.second());
    }

    K first();
    V second();

    @Override
    default K getKey() {
        return first();
    }

    @Override
    default V getValue() {
        return second();
    }

    @Override
    default V setValue(V value) {
        throw new UnsupportedOperationException();
    }

    default Pair<V, K> swap() {
        return Pair.of(second(), first());
    }

    default V coReturn() { return second(); }
    default Pair<K, Pair<K, V>> coJoin() { return Pair.of(first(), this); }
    default <V2> Pair<K, V2> fmap(Function<V, V2> f) { return Pair.of(first(), f.apply(second())); }
    default <V2> Pair<K, V2> coBind(Function<Pair<K, V>, V2> f) {
        return Pair.of(first(), f.apply(this));
    }

    @Override default Pair<K, V> toPair() { return this; }
}

