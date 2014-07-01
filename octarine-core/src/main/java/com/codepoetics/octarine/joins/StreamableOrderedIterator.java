package com.codepoetics.octarine.joins;

import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

interface StreamableOrderedIterator<T> extends Iterator<T> {
    default Stream<T> toStream() {
        return StreamSupport.stream(
                Spliterators.spliteratorUnknownSize(
                        this,
                        Spliterator.IMMUTABLE & Spliterator.NONNULL & Spliterator.ORDERED),
                false);
    }
}
