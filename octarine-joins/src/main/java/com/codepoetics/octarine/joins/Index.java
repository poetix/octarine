package com.codepoetics.octarine.joins;

import com.codepoetics.octarine.functional.tuples.T2;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public final class Index<K, L> {
    private final Comparator<? super K> comparator;
    private final SortedMap<K, Set<L>> indexed;

    private Index(Comparator<? super K> comparator, SortedMap<K, Set<L>> indexed) {
        this.comparator = comparator;
        this.indexed = indexed;
    }

    public static <K extends Comparable<K>, L> Index<K, L> on(Stream<? extends L> stream, Function<? super L, ? extends K> key) {
        return on(stream, key, Comparator.naturalOrder());
    }

    public static <K, L> Index<K, L> on(Stream<? extends L> stream, Function<? super L, ? extends K> key, Comparator<? super K> comparator) {
        return new Index<K, L>(comparator, stream.collect(IndexCollector.<K, L>on(key, comparator)));
    }

    public Spliterator<Map.Entry<K, Set<L>>> entrySpliterator() {
        return indexed.entrySet().spliterator();
    }

    public Set<K> keys() {
        return indexed.keySet();
    }

    public <R> Stream<T2<L, Set<R>>> oneToMany(Index<K, R> other) {
        return matchAndMerge(other, oneToManyMerger());
    }

    public <R> Stream<T2<L, Set<R>>> strictOneToMany(Index<K, R> other) {
        return matchAndMerge(other, strictOneToManyMerger());
    }

    private <R, T> Stream<T> matchAndMerge(Index<K, R> other, BiFunction<Set<L>, Set<R>, Stream<T>> merger) {
        return matchedSublists(other).flatMap(t -> t.sendTo(merger));
    }

    private <R> BiFunction<Set<L>, Set<R>, Stream<T2<L, Set<R>>>> oneToManyMerger() {
        return (lefts, rights) ->
                lefts.stream().map(left -> T2.of(left, rights));
    }

    private <R> BiFunction<Set<L>, Set<R>, Stream<T2<L, Set<R>>>> strictOneToManyMerger() {
        return (lefts, rights) -> {
            if (lefts.isEmpty()) {
                throw new IllegalArgumentException("Unmatched right values found");
            }
            if (lefts.size() > 1) {
                throw new IllegalArgumentException("Duplicate left values found");
            }
            return Stream.of(T2.of(lefts.iterator().next(), rights));
        };
    }

    public <R> Stream<T2<L, R>> manyToOne(Index<K, R> other) {
        return innerJoin(other);
    }

    public <R> Stream<T2<L, R>> strictManyToOne(Index<K, R> other) {
        return matchAndMerge(other, strictManyToOneMerger());
    }

    private <R> BiFunction<Set<L>, Set<R>, Stream<T2<L, R>>> strictManyToOneMerger() {
        return (lefts, rights) -> {
            if (rights.isEmpty()) {
                throw new IllegalArgumentException("Unmatched left values found");
            }
            if (rights.size() > 1) {
                throw new IllegalArgumentException("Duplicate right values found");
            }
            R right = rights.iterator().next();
            return lefts.stream().map(left -> T2.of(left, right));
        };
    }

    public <R> Stream<T2<L, R>> strictOneToOne(Index<K, R> other) {
        return matchAndMerge(other, strictOneToOneMerger());
    }

    private <R> BiFunction<Set<L>, Set<R>, Stream<T2<L, R>>> strictOneToOneMerger() {
        return (lefts, rights) -> {
            if (lefts.isEmpty()) {
                throw new IllegalArgumentException("Unmatched right values found");
            }
            if (lefts.size() > 1) {
                throw new IllegalArgumentException("Duplicate left values found");
            }

            if (rights.isEmpty()) {
                throw new IllegalArgumentException("Unmatched left values found");
            }
            if (rights.size() > 1) {
                throw new IllegalArgumentException("Duplicate right values found");
            }

            return Stream.of(T2.of(lefts.iterator().next(), rights.iterator().next()));
        };
    }

    public <R> Stream<T2<L, Optional<R>>> leftOuterJoin(Index<K, R> other) {
        return matchAndMerge(other, leftOuterJoinMerger());
    }

    private <R> BiFunction<Set<L>, Set<R>, Stream<T2<L, Optional<R>>>> leftOuterJoinMerger() {
        return (lefts, rights) -> {
            if (rights.isEmpty()) {
                return lefts.stream().map(l -> T2.of(l, Optional.<R>empty()));
            }

            return lefts.stream().flatMap(l -> rights.stream().map(r -> T2.of(l, Optional.<R>of(r))));
        };
    }

    public <R> Stream<T2<Optional<L>, R>> rightOuterJoin(Index<K, R> other) {
        return matchAndMerge(other, rightOuterJoinMerger());
    }

    private <R> BiFunction<Set<L>, Set<R>, Stream<T2<Optional<L>, R>>> rightOuterJoinMerger() {
        return (lefts, rights) -> {

            if (lefts.isEmpty()) {
                return rights.stream().map(r -> T2.of(Optional.<L>empty(), r));
            }

            return rights.stream().flatMap(r -> lefts.stream().map(l -> T2.of(Optional.of(l), r)));
        };
    }

    public <R> Stream<T2<L, R>> innerJoin(Index<K, R> other) {
        return matchAndMerge(other, innerJoinMerger());
    }

    private <R> BiFunction<Set<L>, Set<R>, Stream<T2<L, R>>> innerJoinMerger() {
        return (lefts, rights) -> lefts.stream().flatMap(l ->
            rights.stream().map(r -> T2.of(l, r)));
    }

    public <R> Stream<T2<Optional<L>, Optional<R>>> outerJoin(Index<K, R> other) {
        return matchAndMerge(other, outerJoinMerger());
    }

    private <R> BiFunction<Set<L>, Set<R>, Stream<T2<Optional<L>, Optional<R>>>> outerJoinMerger() {
        return (lefts, rights) -> {

            if (lefts.isEmpty()) {
                return rights.stream().map(r -> T2.of(Optional.<L>empty(), Optional.of(r)));
            }

            if (rights.isEmpty()) {
                return lefts.stream().map(l -> T2.of(Optional.of(l), Optional.<R>empty()));
            }

            return lefts.stream().flatMap(l ->
                            rights.stream().map(r ->
                                            T2.of(Optional.<L>of(l), Optional.<R>of(r))
                            )
            );
        };
    }

    private <R> Stream<T2<Set<L>, Set<R>>> matchedSublists(Index<K, R> other) {
        return StreamSupport.stream(KeyMatchingSpliterator.over(
                comparator,
                entrySpliterator(),
                other.entrySpliterator(),
                Collections.emptySet(),
                Collections.emptySet()), false);
    }

    @Override
    public String toString() {
        return indexed.toString();
    }

}
