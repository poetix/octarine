package com.codepoetics.octarine.joins;

import java.util.Comparator;
import java.util.function.Function;
import java.util.stream.Stream;

public final class Joins<L> {

    private final Stream<L> lefts;

    private Joins(Stream<L> lefts) {
        this.lefts = lefts;
    }

    public static <L> Joins<L> join(Stream<L> lefts) {
        return new Joins<>(lefts);
    }

    public <K extends Comparable<K>> JoinBuilder<K, L> on(Function<? super L, ? extends K> foreignKey) {
        return comparingWith(Comparator.<K>naturalOrder()).on(foreignKey);
    }

    public static interface ComparatorCapture<K, L> {
        JoinBuilder<K, L> on(Function<? super L, ? extends K> foreignKey);
    }

    public <K> ComparatorCapture<K, L> comparingWith(Comparator<? super K> comparator) {
        return f -> new JoinBuilder<>(comparator, lefts, JoinKey.on(f, comparator));
    }

    public static final class JoinBuilder<K, L> {
        private final Comparator<? super K> comparator;
        private final Stream<L> lefts;
        private final JoinKey<L, K> foreignKey;

        public JoinBuilder(Comparator<? super K> comparator, Stream<L> lefts, JoinKey<L, K> foreignKey) {
            this.comparator = comparator;
            this.lefts = lefts;
            this.foreignKey = foreignKey;
        }

        public <R> Joiner<L, R, K> to(Function<? super R, ? extends K> primaryKey) {
            return new Joiner<>(foreignKey.index(lefts), JoinKey.on(primaryKey, comparator));
        }
    }
}
