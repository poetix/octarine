package com.codepoetics.octarine.joins;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collector;

final class IndexCollector {
    private IndexCollector() {
    }

    static <K, S> Collector<S, SortedMap<K, Set<S>>, SortedMap<K, Set<S>>> on(
            Function<? super S, ? extends K> key,
            Comparator<? super K> comparator) {
        return Collector.of(
                () -> new TreeMap<>(comparator),
                (map, element) -> map.computeIfAbsent(key.apply(element), k -> new HashSet<>()).add(element),
                IndexCollector::destructiveMergeMaps
        );
    }

    static <K, S> SortedMap<K, Set<S>> destructiveMergeMaps(SortedMap<K, Set<S>> left, SortedMap<K, Set<S>> right) {
        right.entrySet().forEach(e ->
            right.merge(e.getKey(), e.getValue(), IndexCollector::destructiveMergeSets));
        return left;
    }

    static <S> Set<S> destructiveMergeSets(Set<S> left, Set<S> right) {
        left.addAll(right);
        return left;
    }
}
