package com.codepoetics.joink;

import java.util.Collection;

public interface Fetcher<K, R> {
    Collection<? extends R> fetch(Collection<? extends K> keys);
}
