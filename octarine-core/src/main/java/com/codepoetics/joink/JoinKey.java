package com.codepoetics.joink;

import java.util.Collection;
import java.util.Comparator;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toSet;

public interface JoinKey<S, K extends Comparable<K>> extends Function<S, K>, Comparator<S> {

    @Override default int compare(S first, S second) {
        return this.apply(first).compareTo(this.apply(second));
    }

    default Set<K> project(Stream<? extends S> source) {
        return source.map(this).collect(toSet());
    }

    default Index<K, S> index(Collection<? extends S> source) {
        return index(source.parallelStream());
    }

    default Index<K, S> index(Stream<? extends S> source) {
        return Index.on(source, this);
    }
}
