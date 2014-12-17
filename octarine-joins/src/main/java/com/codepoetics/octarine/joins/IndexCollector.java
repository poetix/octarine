package com.codepoetics.octarine.joins;

import java.util.HashSet;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collector;

final class IndexCollector {
    private IndexCollector() {
    }

    static <K extends Comparable<K>, S> Collector<S, SortedMap<K, Set<S>>, SortedMap<K, Set<S>>> on(Function<? super S, ? extends K> key) {
        return Collector.<S, SortedMap<K, Set<S>>>of(
                TreeMap::new,
                (map, element) -> map.computeIfAbsent(key.apply(element), k -> new HashSet<>()).add(element),
                (m1, m2) -> {
                    m2.entrySet().forEach(e ->
                                    m1.merge(e.getKey(), e.getValue(), (s1, s2) -> {
                                        s1.addAll(s2);
                                        return s1;
                                    })
                    );
                    return m1;
                }
        );
    }
}
