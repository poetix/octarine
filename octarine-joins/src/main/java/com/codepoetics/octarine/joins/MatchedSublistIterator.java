package com.codepoetics.octarine.joins;

import com.codepoetics.octarine.functional.tuples.T2;
import com.codepoetics.octarine.utils.PeekableIterator;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

class MatchedSublistIterator<K extends Comparable<K>, L, R> implements StreamableOrderedIterator<T2<Set<L>, Set<R>>> {

    public static <K extends Comparable<K>, L, R> MatchedSublistIterator<K, L, R> over(Index<K, L> self, Index<K, R> other) {
       return new MatchedSublistIterator<K, L, R>(
               PeekableIterator.peeking(self.entries().iterator()),
               PeekableIterator.peeking(other.entries().iterator()));
    }

    private final PeekableIterator<Map.Entry<K, Set<L>>> leftIter;
    private final PeekableIterator<Map.Entry<K, Set<R>>> rightIter;

    MatchedSublistIterator(PeekableIterator<Map.Entry<K, Set<L>>> leftIter, PeekableIterator<Map.Entry<K, Set<R>>> rightIter) {
        this.leftIter = leftIter;
        this.rightIter = rightIter;
    }

    @Override
    public boolean hasNext() {
        return leftIter.hasNext() || rightIter.hasNext();
    }

    @Override
    public T2<Set<L>, Set<R>> next() {
        if (leftIter.hasNext() && rightIter.hasNext()) {
            return alignAndReturnNext();
        }

        return getRemainder();
    }

    private T2<Set<L>, Set<R>> getRemainder() {
        if (leftIter.hasNext()) {
            return leftOnly();
        }

        if (rightIter.hasNext()) {
            return rightOnly();
        }

        return null;
    }

    private T2<Set<L>, Set<R>> alignAndReturnNext() {
        int cmp = compareLeftToRight();

        if (cmp < 0) {
            return leftOnly();
        }

        if (cmp > 0) {
            return rightOnly();
        }

        return T2.of(nextLeft(), nextRight());
    }

    private T2<Set<L>, Set<R>> rightOnly() {
        return T2.of(Collections.emptySet(), nextRight());
    }

    private Set<R> nextRight() {
        return rightIter.next().getValue();
    }

    private T2<Set<L>, Set<R>> leftOnly() {
        return T2.of(nextLeft(), Collections.emptySet());
    }

    private Set<L> nextLeft() {
        return leftIter.next().getValue();
    }

    private int compareLeftToRight() {
        return leftIter.peek().getKey().compareTo(rightIter.peek().getKey());
    }

}
