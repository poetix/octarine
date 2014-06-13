package com.codepoetics.joink;

import java.util.HashSet;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collector;

final class IndexCollector {
    public IndexCollector() { }
    static <K extends Comparable<K>, S> Collector<S, SortedMap<K, Set<S>>, SortedMap<K, Set<S>>> on(Function<? super S, ? extends K> key) {
        return Collector.<S, SortedMap<K, Set<S>>>of(
                TreeMap::new,
                (map, element) -> {
                    K keyValue = key.apply(element);
                    Set<S> elements = map.computeIfAbsent(keyValue, k -> new HashSet<S>());
                    elements.add(element);
                },
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
