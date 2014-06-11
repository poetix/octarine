package com.codepoetics.octarine.records.fixed;

import com.codepoetics.octarine.records.Key;
import org.pcollections.HashTreePMap;
import org.pcollections.HashTreePSet;
import org.pcollections.PMap;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

class KeyIndex {

    private static final ConcurrentMap<Collection<Key<?>>, KeyIndex> cache = new ConcurrentHashMap<>();

    public static KeyIndex on(Collection<Key<?>> keys) {
        return cache.computeIfAbsent(keys, ks -> {
            Map<Key<?>, Integer> lookupBuilder = new HashMap<>();
            int i = 0;
            for (Key<?> key : keys) {
                lookupBuilder.put(key, i++);
            }
            return new KeyIndex(HashTreePMap.from(lookupBuilder));
        });
    }

    private final Map<Key<?>, Integer> lookup;

    private KeyIndex(Map<Key<?>, Integer> lookup) {
        this.lookup = lookup;
    }

    public Optional<Integer> lookup(Key<?> key) {
        return Optional.ofNullable(lookup.get(key));
    }

    public PMap<Key<?>, Object> valuesFrom(Object[] values) {
       Map<Key<?>, Object> valuesBuilder = new HashMap<>();
       return HashTreePMap.from(lookup.entrySet().stream().collect(Collectors.toMap(
               Map.Entry::getKey,
               e -> values[e.getValue()]
       )));
    }

    public Object[] arrayFrom(PMap<Key<?>, Object> values) {

        if (!values.keySet().equals(lookup.keySet())) {
            throw new IllegalArgumentException(
                    String.format("Values contains keys not in index: %s",
                    HashTreePSet.from(values.keySet()).minusAll(lookup.keySet())));
        }

        Object[] result = new Object[values.size()];

        for (Map.Entry<Key<?>, Object> entry : values.entrySet()) {
            result[lookup(entry.getKey()).get()] = entry.getValue();
        }

        return result;
    }
}
