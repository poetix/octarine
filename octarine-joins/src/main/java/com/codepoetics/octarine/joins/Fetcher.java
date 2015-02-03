package com.codepoetics.octarine.joins;

import java.util.Collection;
import java.util.stream.Stream;

public interface Fetcher<K, R> {
    Stream<? extends R> fetch(Collection<? extends K> keys);
}
