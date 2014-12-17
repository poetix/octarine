package com.codepoetics.octarine.joins;

import java.util.Collection;

public interface Fetcher<K, R> {
    Collection<? extends R> fetch(Collection<? extends K> keys);
}
