package com.codepoetics.octarine.morphisms;

import com.codepoetics.octarine.com.codepoetics.octarine.tuples.Pair;
import org.pcollections.HashTreePMap;
import org.pcollections.PMap;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public interface FluentMap<K, V> {

    static <K, V> FluentMap<K, V> from(Map<K, V> map) {
        return new FluentMap<K, V>() {
            @Override public Map<K, V> toMap() { return map; }
        };
    }

    static <K, V> FluentMap<K, V> from(PMap<K, V> map) {
        return new FluentMap<K, V>() {
            @Override public Map<K, V> toMap() { return map; }
            @Override public PMap<K, V> toPMap() { return map; }
        };
    }

    static <K, V> FluentMap<K, V> from(Stream<Pair<K, V>> pairs) {
        Map<K, V> result = new HashMap<>();
        pairs.forEach(p -> result.put(p.first(), p.second()));
        return from(result);
    }

    Map<K, V> toMap();

    default PMap<K, V> toPMap() {
        Map<K, V> asMap = toMap();
        return asMap instanceof PMap ? (PMap<K, V>) asMap : HashTreePMap.from(toMap());
    }
    default Stream<Pair<K, V>> toPairs() { return toMap().entrySet().stream().map(Pair::from); }

}
