package com.codepoetics.octarine.morphisms;

import org.pcollections.*;

import java.util.*;

public final class PCollectionBijection {

    private PCollectionBijection() { }

    public static <K, V> Bijection<PMap<K, V>, Map<K, V>> pmapToMap() {
        return Bijection.of(
                HashMap::new,
                HashTreePMap::from
        );
    }

    public static <T> Bijection<PVector<T>, List<T>> pvectorToList() {
        return Bijection.of(
                ArrayList::new,
                TreePVector::from
        );
    }

    public static <T> Bijection<PSet<T>, Set<T>> psetToSet() {
         return Bijection.of(
                HashSet::new,
                HashTreePSet::from
         );
    }
}
