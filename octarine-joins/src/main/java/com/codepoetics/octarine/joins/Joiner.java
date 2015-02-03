package com.codepoetics.octarine.joins;

import com.codepoetics.octarine.functional.tuples.T2;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Stream;

public final class Joiner<L, R, K> {

    private final Index<K, L> leftIndex;
    private final JoinKey<R, K> primaryKey;

    public Joiner(Index<K, L> leftIndex, JoinKey<R, K> primaryKey) {
        this.leftIndex = leftIndex;
        this.primaryKey = primaryKey;
    }

    // Many to one
    public Stream<T2<L, R>> manyToOne(Collection<? extends R> rights) {
        return manyToOne(rights.stream());
    }

    public Stream<T2<L, R>> manyToOne(Fetcher<K, R> fetcher) {
        return manyToOne(fetcher.fetch(leftIndex.keys()));
    }

    public Stream<T2<L, R>> manyToOne(Stream<? extends R> rights) {
        return leftIndex.manyToOne(primaryKey.index(rights));
    }

    // Strict many to one
    public Stream<T2<L, R>> strictManyToOne(Collection<? extends R> rights) {
        return strictManyToOne(rights.stream());
    }

    public Stream<T2<L, R>> strictManyToOne(Fetcher<K, R> fetcher) {
        return strictManyToOne(fetcher.fetch(leftIndex.keys()));
    }

    public Stream<T2<L, R>> strictManyToOne(Stream<? extends R> rights) {
        return leftIndex.strictManyToOne(primaryKey.index(rights));
    }

    // One to many
    public Stream<T2<L, Set<R>>> oneToMany(Collection<? extends R> rights) {
        return oneToMany(rights.stream());
    }

    public Stream<T2<L, Set<R>>> oneToMany(Fetcher<K, R> fetcher) {
        return oneToMany(fetcher.fetch(leftIndex.keys()));
    }

    public Stream<T2<L, Set<R>>> oneToMany(Stream<? extends R> rights) {
        return leftIndex.oneToMany(primaryKey.index(rights));
    }

    // Strict one to many
    public Stream<T2<L, Set<R>>> strictOneToMany(Collection<? extends R> rights) {
        return strictOneToMany(rights.stream());
    }

    public Stream<T2<L, Set<R>>> strictOneToMany(Fetcher<K, R> fetcher) {
        return strictOneToMany(fetcher.fetch(leftIndex.keys()));
    }

    public Stream<T2<L, Set<R>>> strictOneToMany(Stream<? extends R> rights) {
        return leftIndex.strictOneToMany(primaryKey.index(rights));
    }

    // Strict one to one
    public Stream<T2<L, R>> strictOneToOne(Collection<? extends R> rights) {
        return strictOneToOne(rights.stream());
    }

    public Stream<T2<L, R>> strictOneToOne(Fetcher<K, R> fetcher) {
        return strictOneToOne(fetcher.fetch(leftIndex.keys()));
    }

    public Stream<T2<L, R>> strictOneToOne(Stream<? extends R> rights) {
        return leftIndex.strictOneToOne(primaryKey.index(rights));
    }

}
