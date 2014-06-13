package com.codepoetics.joink;

import java.util.Collection;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

public class Joiner<L, R, K extends Comparable<K>> {

    private final Index<K, L> leftIndex;
    private final JoinKey<R, K> primaryKey;

    public Joiner(Index<K, L> leftIndex, JoinKey<R, K> primaryKey) {
        this.leftIndex = leftIndex;
        this.primaryKey = primaryKey;
    }

    public Stream<Tuple2<L, R>> manyToOne(Collection<? extends R> rights) {
        return merge(rights, leftIndex::manyToOne);
    }

    public Stream<Tuple2<L, R>> manyToOne(Stream<R> rights) {
        return merge(rights, leftIndex::manyToOne);
    }

    public Stream<Tuple2<L, R>> manyToOne(Fetcher<K, R> fetcher) {
        return fetchAndMerge(fetcher, leftIndex::manyToOne);
    }


    public Stream<Tuple2<L, R>> strictManyToOne(Collection<? extends R> rights) {
        return merge(rights, leftIndex::strictManyToOne);
    }

    public Stream<Tuple2<L, R>> strictManyToOne(Stream<R> rights) {
        return merge(rights, leftIndex::strictManyToOne);
    }

    public Stream<Tuple2<L, R>> strictManyToOne(Fetcher<K, R> fetcher) {
        return fetchAndMerge(fetcher, leftIndex::strictManyToOne);
    }

    public Stream<Tuple2<L, Set<R>>> oneToMany(Collection<? extends R> rights) {
        return merge(rights, leftIndex::oneToMany);
    }

    public Stream<Tuple2<L, Set<R>>> oneToMany(Stream<R> rights) {
        return merge(rights, leftIndex::oneToMany);
    }

    public Stream<Tuple2<L, Set<R>>> oneToMany(Fetcher<K, R> fetcher) {
        return fetchAndMerge(fetcher, leftIndex::oneToMany);
    }

    public Stream<Tuple2<L, Set<R>>> strictOneToMany(Collection<? extends R> rights) {
        return merge(rights, leftIndex::strictOneToMany);
    }

    public Stream<Tuple2<L, Set<R>>> strictOneToMany(Stream<R> rights) {
        return merge(rights, leftIndex::strictOneToMany);
    }

    public Stream<Tuple2<L, Set<R>>> strictOneToMany(Fetcher<K, R> fetcher) {
        return fetchAndMerge(fetcher, leftIndex::strictOneToMany);
    }

    public Stream<Tuple2<L, R>> strictOneToOne(Collection<? extends R> rights) {
        return merge(rights, leftIndex::strictOneToOne);
    }

    public Stream<Tuple2<L, R>> strictOneToOne(Stream<R> rights) {
        return merge(rights, leftIndex::strictOneToOne);
    }

    public Stream<Tuple2<L, R>> strictOneToOne(Fetcher<K, R> fetcher) {
        return fetchAndMerge(fetcher, leftIndex::strictOneToOne);
    }

    private <RS> Stream<Tuple2<L, RS>> merge(Stream<? extends R> rights, Function<Index<K, R>, Stream<Tuple2<L, RS>>> merger) {
        return merger.apply(primaryKey.index(rights));
    }

    private <RS> Stream<Tuple2<L, RS>> merge(Collection<? extends R> rights, Function<Index<K, R>, Stream<Tuple2<L, RS>>> merger) {
        return merge(rights.stream(), merger);
    }

    private <RS> Stream<Tuple2<L, RS>> fetchAndMerge(Fetcher<K, R> fetcher, Function<Index<K, R>, Stream<Tuple2<L, RS>>> merger) {
        Collection<? extends R> rights = fetcher.fetch(leftIndex.keys());
        return merge(rights, merger);
    }
}
