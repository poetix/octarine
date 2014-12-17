package com.codepoetics.octarine.joins;

import com.codepoetics.octarine.keys.Key;
import com.codepoetics.octarine.records.Record;

import java.util.Collection;
import java.util.stream.Stream;

public final class RecordJoins {

    private final Stream<? extends Record> lefts;

    public RecordJoins(Stream<? extends Record> lefts) {
        this.lefts = lefts;
    }

    public static RecordJoins join(Collection<? extends Record> lefts) {
        return join(lefts.parallelStream());
    }

    public static RecordJoins join(Stream<? extends Record> lefts) {
        return new RecordJoins(lefts);
    }

    public <K extends Comparable<K>> JoinBuilder<K> on(Key<K> foreignKey) {
        return new JoinBuilder<K>(foreignKey::extract);
    }

    public class JoinBuilder<K extends Comparable<K>> {
        private final JoinKey<Record, K> foreignKey;

        public JoinBuilder(JoinKey<Record, K> foreignKey) {
            this.foreignKey = foreignKey;
        }

        public RecordJoiner<K> to(Key<? extends K> primaryKey) {
            return new RecordJoiner<K>(foreignKey.index(lefts), primaryKey::extract);
        }
    }
}
