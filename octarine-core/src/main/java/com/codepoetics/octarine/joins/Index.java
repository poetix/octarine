package com.codepoetics.octarine.joins;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class Index<K extends Comparable<K>, L> {
    static <K extends Comparable<K>, L> Index<K, L> on(Stream<? extends L> stream, Function<? super L, ? extends K> key) {
        return new Index<K, L>(stream.collect(IndexCollector.on(key)));
    }

    private final SortedMap<K, Set<L>> indexed;
    public Index(SortedMap<K, Set<L>> indexed) {
        this.indexed = indexed;
    }

    public Set<Map.Entry<K, Set<L>>> entries() {
        return indexed.entrySet();
    }

    public Set<K> keys() {
        return indexed.keySet();
    }

    public Stream<L> values() {
        return indexed.values().stream().flatMap(s -> s.stream());
    }

    public <R> Stream<Tuple2<L, Set<R>>> oneToMany(Index<K, R> other) {
        return matchAndMerge(other, oneToManyMerger());
    }

    public <R> Stream<Tuple2<L, Set<R>>> strictOneToMany(Index<K, R> other) {
        return matchAndMerge(other, strictOneToManyMerger());
    }
    
    private <R, T> Stream<T> matchAndMerge(Index<K, R> other, BiFunction<Set<L>, Set<R>, Stream<T>> merger) {
        return matchedSublists(other).flatMap(Tuple2.mergingWith(merger));
    }
                
    private <R> BiFunction<Set<L>, Set<R>, Stream<Tuple2<L, Set<R>>>> oneToManyMerger() {
        return (lefts, rights) ->
            lefts.stream().map(left -> Tuple2.of(left, rights));
    }

    private <R> BiFunction<Set<L>, Set<R>, Stream<Tuple2<L, Set<R>>>> strictOneToManyMerger() {
        return (lefts, rights) -> {
            if (lefts.size() == 0) throw new IllegalArgumentException("Unmatched right values found");
            if (lefts.size() > 1) throw new IllegalArgumentException("Duplicate left values found");
            return Stream.of(Tuple2.of(lefts.iterator().next(), rights));
        };
    }

    public <R> Stream<Tuple2<L, R>> manyToOne(Index<K, R> other) {
        return matchAndMerge(other, manyToOneMerger());
    }

    public <R> Stream<Tuple2<L, R>> strictManyToOne(Index<K, R> other) {
        return matchAndMerge(other, strictManyToOneMerger());
    }

    private <R> BiFunction<Set<L>, Set<R>, Stream<Tuple2<L, R>>> manyToOneMerger() {
        return (lefts, rights) ->
            lefts.stream().flatMap(l ->
                    rights.stream().map(r -> Tuple2.of(l, r)));
    }

    private <R> BiFunction<Set<L>, Set<R>, Stream<Tuple2<L, R>>> strictManyToOneMerger() {
        return (lefts, rights) -> {
            if (rights.size() == 0) throw new IllegalArgumentException("Unmatched left values found");
            if (rights.size() > 1) throw new IllegalArgumentException(("Duplicate right values found"));
            R right = rights.iterator().next();
            return lefts.stream().map(left -> Tuple2.of(left, right));
        };
    }

    public <R> Stream<Tuple2<L, R>> strictOneToOne(Index<K, R> other) {
        return matchAndMerge(other, strictOneToOneMerger());
    }

    private <R> BiFunction<Set<L>, Set<R>, Stream<Tuple2<L, R>>> strictOneToOneMerger() {
        return (lefts, rights) -> {
            if (lefts.size() == 0) throw new IllegalArgumentException("Unmatched right values found");
            if (lefts.size() > 1) throw new IllegalArgumentException(("Duplicate left values found"));

            if (rights.size() == 0) throw new IllegalArgumentException("Unmatched left values found");
            if (rights.size() > 1) throw new IllegalArgumentException(("Duplicate right values found"));

            return Stream.of(Tuple2.of(lefts.iterator().next(), rights.iterator().next()));
        };
    }

    public <R> Stream<Tuple2<L, Optional<R>>> leftOuterJoin(Index<K, R> other) {
        return matchAndMerge(other, leftOuterJoinMerger());
    }

    private <R> BiFunction<Set<L>, Set<R>, Stream<Tuple2<L, Optional<R>>>> leftOuterJoinMerger() {
        return (lefts, rights) -> {
            if (rights.isEmpty()) {
                return lefts.stream().map(l -> Tuple2.of(l, Optional.<R>empty()));
            }

            return lefts.stream().flatMap(l ->
                            rights.stream().map(r ->
                                            Tuple2.of(l, Optional.<R>of(r))
                            )
            );
        };
    }

    public <R> Stream<Tuple2<Optional<L>, R>> rightOuterJoin(Index<K, R> other) {
        return matchAndMerge(other, rightOuterJoinMerger());
    }

    private  <R> BiFunction<Set<L>, Set<R>, Stream<Tuple2<Optional<L>, R>>> rightOuterJoinMerger() {
        return (lefts, rights) -> {

            if (lefts.isEmpty()) {
                return rights.stream().map(r -> Tuple2.of(Optional.<L>empty(), r));
            }

            return rights.stream().flatMap(r ->
                            lefts.stream().map(l ->
                                            Tuple2.of(Optional.of(l), r)
                            )
            );
        };
    }

    public <R> Stream<Tuple2<L, R>> innerJoin(Index<K, R> other) {
        return matchAndMerge(other, innerJoinMerger());
    }

    private  <R> BiFunction<Set<L>, Set<R>, Stream<Tuple2<L, R>>> innerJoinMerger() {
        return (lefts, rights) -> {
            return lefts.stream().flatMap(l ->
                            rights.stream().map(r ->
                                            Tuple2.of(l, r)
                            )
            );
        };
    }

    public <R> Stream<Tuple2<Optional<L>, Optional<R>>> outerJoin(Index<K, R> other) {
        return matchAndMerge(other, outerJoinMerger());
    }

    private <R> BiFunction<Set<L>, Set<R>, Stream<Tuple2<Optional<L>, Optional<R>>>> outerJoinMerger() {
        return (lefts, rights) -> {

            if (lefts.isEmpty()) {
                return rights.stream().map(r -> Tuple2.of(Optional.<L>empty(), Optional.of(r)));
            }

            if (rights.isEmpty()) {
                return lefts.stream().map(l -> Tuple2.of(Optional.of(l), Optional.<R>empty()));
            }

            return lefts.stream().flatMap(l ->
                 rights.stream().map(r ->
                     Tuple2.of(Optional.<L>of(l), Optional.<R>of(r))
                 )
            );
        };
    }

    private <R> Stream<Tuple2<Set<L>, Set<R>>> matchedSublists(Index<K, R> other) {
        PeekableIterator<Map.Entry<K, Set<L>>> leftIter = PeekableIterator.peeking(entries().iterator());
        PeekableIterator<Map.Entry<K, Set<R>>> rightIter = PeekableIterator.peeking(other.entries().iterator());

        Iterator<Tuple2<Set<L>, Set<R>>> matchedIter = new Iterator<Tuple2<Set<L>, Set<R>>>() {
            @Override
            public boolean hasNext() {
                return leftIter.hasNext() || rightIter.hasNext();
            }

            @Override
            public Tuple2<Set<L>, Set<R>> next() {
                if (!leftIter.hasNext() && !rightIter.hasNext()) { return null; }
                if (!leftIter.hasNext()) { return Tuple2.of(Collections.emptySet(), rightIter.next().getValue()); }
                if (!rightIter.hasNext()) { return Tuple2.of(leftIter.next().getValue(), Collections.emptySet()); }

                int cmp = (leftIter.peek().getKey().compareTo(rightIter.peek().getKey()));
                if (cmp < 0) {
                    return Tuple2.of(leftIter.next().getValue(), Collections.emptySet());
                }
                if (cmp == 0) {
                    return Tuple2.of(leftIter.next().getValue(), rightIter.next().getValue());
                }
                return Tuple2.of(Collections.emptySet(), rightIter.next().getValue());
            }
        };

        return StreamSupport.stream(
                Spliterators.spliteratorUnknownSize(
                        matchedIter,
                        Spliterator.IMMUTABLE & Spliterator.NONNULL & Spliterator.ORDERED),
                false);
    }

    @Override public String toString() {
        return indexed.toString();
    }

}
