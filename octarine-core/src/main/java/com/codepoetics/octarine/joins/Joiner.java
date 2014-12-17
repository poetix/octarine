package com.codepoetics.octarine.joins;

import com.codepoetics.octarine.tuples.T2;

import java.util.Collection;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

public final class Joiner<L, R, K extends Comparable<K>> {

    private final Index<K, L> leftIndex;
    private final JoinKey<R, K> primaryKey;

    public Joiner(Index<K, L> leftIndex, JoinKey<R, K> primaryKey) {
        this.leftIndex = leftIndex;
        this.primaryKey = primaryKey;
    }

    @SuppressWarnings("unchecked")
    public Stream<T2<L, R>> manyToOne(Collection<? extends R> rights) {
        return merge(rights, leftIndex::manyToOne);
    }

    @SuppressWarnings("unchecked")
    public Stream<T2<L, R>> manyToOne(Stream<R> rights) {
        return merge(rights, leftIndex::manyToOne);
    }

    @SuppressWarnings("unchecked")
    public Stream<T2<L, R>> manyToOne(Fetcher<K, R> fetcher) {
        return fetchAndMerge(fetcher, leftIndex::manyToOne);
    }


    @SuppressWarnings("unchecked")
    public Stream<T2<L, R>> strictManyToOne(Collection<? extends R> rights) {
        return merge(rights, leftIndex::strictManyToOne);
    }

    @SuppressWarnings("unchecked")
    public Stream<T2<L, R>> strictManyToOne(Stream<R> rights) {
        return merge(rights, leftIndex::strictManyToOne);
    }

    @SuppressWarnings("unchecked")
    public Stream<T2<L, R>> strictManyToOne(Fetcher<K, R> fetcher) {
        return fetchAndMerge(fetcher, leftIndex::strictManyToOne);
    }

    @SuppressWarnings("unchecked")
    public Stream<T2<L, Set<R>>> oneToMany(Collection<? extends R> rights) {
        return merge(rights, leftIndex::oneToMany);
    }

    @SuppressWarnings("unchecked")
    public Stream<T2<L, Set<R>>> oneToMany(Stream<R> rights) {
        return merge(rights, leftIndex::oneToMany);
    }

    @SuppressWarnings("unchecked")
    public Stream<T2<L, Set<R>>> oneToMany(Fetcher<K, R> fetcher) {
        return fetchAndMerge(fetcher, leftIndex::oneToMany);
    }

    @SuppressWarnings("unchecked")
    public Stream<T2<L, Set<R>>> strictOneToMany(Collection<? extends R> rights) {
        return merge(rights, leftIndex::strictOneToMany);
    }

    @SuppressWarnings("unchecked")
    public Stream<T2<L, Set<R>>> strictOneToMany(Stream<R> rights) {
        return merge(rights, leftIndex::strictOneToMany);
    }

    @SuppressWarnings("unchecked")
    public Stream<T2<L, Set<R>>> strictOneToMany(Fetcher<K, R> fetcher) {
        return fetchAndMerge(fetcher, leftIndex::strictOneToMany);
    }

    @SuppressWarnings("unchecked")
    public Stream<T2<L, R>> strictOneToOne(Collection<? extends R> rights) {
        return merge(rights, leftIndex::strictOneToOne);
    }

    @SuppressWarnings("unchecked")
    public Stream<T2<L, R>> strictOneToOne(Stream<R> rights) {
        return merge(rights, leftIndex::strictOneToOne);
    }

    @SuppressWarnings("unchecked")
    public Stream<T2<L, R>> strictOneToOne(Fetcher<K, R> fetcher) {
        return fetchAndMerge(fetcher, leftIndex::strictOneToOne);
    }

    private <RS> Stream<T2<L, RS>> merge(Stream<? extends R> rights, Function<Index<K, R>, Stream<T2<L, RS>>> merger) {
        return merger.apply(primaryKey.index(rights));
    }

    private <RS> Stream<T2<L, RS>> merge(Collection<? extends R> rights, Function<Index<K, R>, Stream<T2<L, RS>>> merger) {
        return merge(rights.stream(), merger);
    }

    private <RS> Stream<T2<L, RS>> fetchAndMerge(Fetcher<K, R> fetcher, Function<Index<K, R>, Stream<T2<L, RS>>> merger) {
        Collection<? extends R> rights = fetcher.fetch(leftIndex.keys());
        return merge(rights, merger);
    }
}
