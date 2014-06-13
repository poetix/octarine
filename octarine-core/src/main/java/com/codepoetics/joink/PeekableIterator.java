package com.codepoetics.joink;

import java.util.Iterator;

public interface PeekableIterator<T> extends Iterator<T> {
    static <T> PeekableIterator<T> peeking(Iterator<T> iterator) {
        return new PeekableIterator<T>() {
            private T buffer = null;
            @Override
            public T peek() {
                if (buffer == null) {
                    buffer = iterator.hasNext() ? iterator.next() : null;
                }
                return buffer;
            }

            @Override
            public boolean hasNext() {
                return buffer != null || iterator.hasNext();
            }

            @Override
            public T next() {
                T result = buffer == null ? iterator.next() : buffer;
                buffer = null;
                return result;
            }
        };
    }
    T peek();
}
