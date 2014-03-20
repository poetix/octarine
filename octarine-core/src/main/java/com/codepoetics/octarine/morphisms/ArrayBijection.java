package com.codepoetics.octarine.morphisms;

import java.util.*;
import java.util.stream.Stream;

public final class ArrayBijection {

    private ArrayBijection() { }

    public static <A> Bijection<A[], List<A>> arrayToList() {
        return arrayToStream().andThen(StreamBijection.listToStream().reverse());
    }

    public static <A> Bijection<A[], Set<A>> arrayToSet() {
        return Bijection.<List<A>, Set<A>>of(
                HashSet::new,
                Set::stream
        ).compose(arrayToList());
    }

    public static <T> Bijection<T[], Stream<T>> arrayToStream() {
        return Bijection.of(
                Arrays::stream,
                s -> (T[]) s.toArray()
        );
    }
}
