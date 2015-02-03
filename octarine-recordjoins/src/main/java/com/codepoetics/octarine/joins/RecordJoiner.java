package com.codepoetics.octarine.joins;

import com.codepoetics.octarine.functional.tuples.T2;
import com.codepoetics.octarine.records.ListKey;
import com.codepoetics.octarine.records.Record;

import java.util.Collection;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

public final class RecordJoiner<K> {

    private final Index<K, Record> leftIndex;
    private final JoinKey<Record, K> primaryKey;

    public RecordJoiner(Index<K, Record> leftIndex, JoinKey<Record, K> primaryKey) {
        this.leftIndex = leftIndex;
        this.primaryKey = primaryKey;
    }

    // Many to one
    public Stream<Record> manyToOne(Collection<? extends Record> rights) {
        return manyToOne(rights.stream());
    }

    public Stream<Record> manyToOne(Fetcher<K, Record> fetcher) {
        return manyToOne(fetcher.fetch(leftIndex.keys()));
    }

    public Stream<Record> manyToOne(Stream<? extends Record> rights) {
        return leftIndex.manyToOne(primaryKey.index(rights)).map(recordIntoRecord());
    }

    // Strict many to one
    public Stream<Record> strictManyToOne(Collection<? extends Record> rights) {
        return strictManyToOne(rights.stream());
    }

    public Stream<Record> strictManyToOne(Fetcher<K, Record> fetcher) {
        return strictManyToOne(fetcher.fetch(leftIndex.keys()));
    }

    public Stream<Record> strictManyToOne(Stream<? extends Record> rights) {
        return leftIndex.strictManyToOne(primaryKey.index(rights)).map(recordIntoRecord());
    }

    // One to many
    public Stream<Record> oneToMany(Collection<? extends Record> rights, ListKey<Record> manyKey) {
        return oneToMany(rights.stream(), manyKey);
    }

    public Stream<Record> oneToMany(Fetcher<K, Record> fetcher, ListKey<Record> manyKey) {
        return oneToMany(fetcher.fetch(leftIndex.keys()), manyKey);
    }

    public Stream<Record> oneToMany(Stream<? extends Record> rights, ListKey<Record> manyKey) {
        return leftIndex.oneToMany(primaryKey.index(rights)).map(recordsIntoRecord(manyKey));
    }

    // Strict one to many
    public Stream<Record> strictOneToMany(Collection<? extends Record> rights, ListKey<Record> manyKey) {
        return strictOneToMany(rights.stream(), manyKey);
    }

    public Stream<Record> strictOneToMany(Fetcher<K, Record> fetcher, ListKey<Record> manyKey) {
        return strictOneToMany(fetcher.fetch(leftIndex.keys()), manyKey);
    }

    public Stream<Record> strictOneToMany(Stream<? extends Record> rights, ListKey<Record> manyKey) {
        return leftIndex.strictOneToMany(primaryKey.index(rights)).map(recordsIntoRecord(manyKey));
    }

    // Strict one to one
    public Stream<Record> strictOneToOne(Collection<? extends Record> rights) {
        return strictOneToOne(rights.stream());
    }

    public Stream<Record> strictOneToOne(Fetcher<K, Record> fetcher) {
        return strictOneToOne(fetcher.fetch(leftIndex.keys()));
    }

    public Stream<Record> strictOneToOne(Stream<? extends Record> rights) {
        return leftIndex.strictOneToOne(primaryKey.index(rights)).map(recordIntoRecord());
    }

    private Function<T2<Record, Record>, Record> recordIntoRecord() {
        return t -> t.sendTo(Record::with);
    }

    private Function<T2<Record, Set<Record>>, Record> recordsIntoRecord(ListKey<Record> key) {
        return t -> t.sendTo((f, s) -> f.with(key.of(s)));
    }
}
