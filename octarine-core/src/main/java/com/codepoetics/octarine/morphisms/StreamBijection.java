package com.codepoetics.octarine.morphisms;

import org.pcollections.*;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class StreamBijection {

    private StreamBijection() { }

    public static <T> Bijection<Stream<T>, List<T>> toList() {
        return Bijection.of(
            s -> s.collect(Collectors.toList()),
            List::stream
        );
    }

    public static <T> Bijection<Stream<T>, Set<T>> toSet() {
        return Bijection.of(
            s -> s.collect(Collectors.toSet()),
            Set::stream
        );
    }

    public static <T> Bijection<Stream<T>, PSet<T>> toPSet() {
        return Bijection.<Set<T>, PSet<T>>of(
                HashTreePSet::from,
                Function.identity()
        ).compose(toSet());
    }

    public static <T> Bijection<Stream<T>, PVector<T>> toPVector() {
        return Bijection.<List<T>, PVector<T>>of(
                TreePVector::from,
                Function.identity()
        ).compose(toList());
    }

    public static <K, V> Bijection<Stream<Map.Entry<K,V >>, Map<K, V>> toMap() {
        return Bijection.of(
            s -> {
                Map<K, V> result = new HashMap<>();
                s.forEach(e -> result.put(e.getKey(), e.getValue()));
                return result;
            },
             m -> m.entrySet().stream()
        );
    }

    public static <K, V> Bijection<Stream<Map.Entry<K,V >>, PMap<K, V>> toPMap() {
        return Bijection.<Map<K, V>, PMap<K, V>>of(
            HashTreePMap::from,
            Function.identity()
        ).compose(toMap());
    }

}
