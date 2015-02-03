package com.codepoetics.octarine.joins;

import java.util.Collection;
import java.util.Comparator;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toSet;

public interface JoinKey<S, K> extends Function<S, K>, Comparator<S> {

    static <S, K> JoinKey<S, K> on(Function<? super S, ? extends K> f, Comparator<? super K> comparator) {
        return new JoinKey<S, K>() {
            @Override
            public K apply(S s) {
                return f.apply(s);
            }

            @Override
            public int compare(S o1, S o2) {
                return comparator.compare(apply(o1), apply(o2));
            }

            @Override
            public Index<K, S> index(Stream<? extends S> source) {
                return Index.on(source, f, comparator);
            }
        };
    }

    default Set<K> project(Stream<? extends S> source) {
        return source.map(this).collect(toSet());
    }

    default Index<K, S> index(Collection<? extends S> source) {
        return index(source.stream());
    }

    Index<K, S> index(Stream<? extends S> source);
}
