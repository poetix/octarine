package com.codepoetics.octarine.joins;

import com.codepoetics.octarine.records.Key;
import com.codepoetics.octarine.records.Record;

import java.util.Collection;
import java.util.Comparator;
import java.util.function.Function;
import java.util.stream.Stream;

public final class RecordJoins {

    private final Stream<? extends Record> lefts;

    public RecordJoins(Stream<? extends Record> lefts) {
        this.lefts = lefts;
    }

    public static RecordJoins join(Collection<? extends Record> lefts) {
        return join(lefts.stream());
    }

    public static RecordJoins join(Stream<? extends Record> lefts) {
        return new RecordJoins(lefts);
    }

    public <K extends Comparable<K>> JoinBuilder<K> on(Key<K> foreignKey) {
        return comparingWith(Comparator.<K>naturalOrder()).on(foreignKey::extract);
    }

    public static interface ComparatorCapture<K> {
        JoinBuilder<K> on(Function<? super Record, ? extends K> foreignKey);
    }

    public <K> ComparatorCapture<K> comparingWith(Comparator<? super K> comparator) {
        return f -> new JoinBuilder<>(comparator, lefts, JoinKey.on(f, comparator));
    }

    public static final class JoinBuilder<K> {
        private final Comparator<? super K> comparator;
        private final Stream<? extends Record> lefts;
        private final JoinKey<Record, K> foreignKey;

        private JoinBuilder(Comparator<? super K> comparator,
                            Stream<? extends Record> lefts,
                            JoinKey<Record, K> foreignKey) {
            this.comparator = comparator;
            this.lefts = lefts;
            this.foreignKey = foreignKey;
        }

        public RecordJoiner<K> to(Key<? extends K> primaryKey) {
            return new RecordJoiner<K>(foreignKey.index(lefts), JoinKey.<Record, K>on(primaryKey::extract, comparator));
        }
    }
}
