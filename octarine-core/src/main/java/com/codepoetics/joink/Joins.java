package com.codepoetics.joink;

import java.util.stream.Stream;

public class Joins<L> {

    public static <L> Joins<L> join(Stream<L> lefts) {
        return new Joins<>(lefts);
    }

    private final Stream<L> lefts;

    public Joins(Stream<L> lefts) {
        this.lefts = lefts;
    }

    public <K extends Comparable<K>> JoinBuilder<K> on(JoinKey<L, K> foreignKey) {
        return new JoinBuilder<K>(foreignKey);
    }

    public class JoinBuilder<K extends Comparable<K>> {
         private final JoinKey<L, K> foreignKey;

        public JoinBuilder(JoinKey<L, K> foreignKey) {
            this.foreignKey = foreignKey;
        }

        public <R> Joiner<L, R, K> to(JoinKey<R, K> primaryKey) {
            return new Joiner<>(foreignKey.index(lefts), primaryKey);
        }
    }
}
