package com.codepoetics.octarine.joins;

import com.codepoetics.octarine.functional.tuples.T2;

import java.util.*;
import java.util.function.Consumer;

class MatchedSublistSpliterator<K, L, R> implements Spliterator<T2<Set<L>, Set<R>>> {

    public static <K, L, R> MatchedSublistSpliterator<K, L, R> over(
            Index<K, L> self,
            Index<K, R> other,
            Comparator<? super K> comparator) {
       return new MatchedSublistSpliterator<K, L, R>(
               comparator,
               self.spliterator(),
               other.spliterator());
    }

    private final Comparator<? super K> comparator;
    private final Spliterator<Map.Entry<K, Set<L>>> leftIter;
    private final Spliterator<Map.Entry<K, Set<R>>> rightIter;
    private boolean initialised = false;
    private Optional<Map.Entry<K, Set<L>>> lastLeft = Optional.empty();
    private Optional<Map.Entry<K, Set<R>>> lastRight = Optional.empty();

    MatchedSublistSpliterator(Comparator<? super K> comparator,
                              Spliterator<Map.Entry<K, Set<L>>> leftIter,
                              Spliterator<Map.Entry<K, Set<R>>> rightIter) {
        this.comparator = comparator;
        this.leftIter = leftIter;
        this.rightIter = rightIter;
    }


    @Override
    public boolean tryAdvance(Consumer<? super T2<Set<L>, Set<R>>> action) {
        if (!initialised) {
            leftIter.tryAdvance(left -> lastLeft = Optional.of(left));
            rightIter.tryAdvance(right -> lastRight = Optional.of(right));
            initialised = true;
        }

        if (!(lastLeft.isPresent() || lastRight.isPresent())) {
            return false;
        }

        if (lastLeft.isPresent() && lastRight.isPresent()) {
            lastLeft.ifPresent(left -> lastRight.ifPresent(right -> {
                int cmp = comparator.compare(left.getKey(), right.getKey());

                if (cmp < 0) {
                    sendLeft(action, left);
                }

                if (cmp > 0) {
                    sendRight(action, right);
                }

                if (cmp == 0) {
                    sendBoth(action, left, right);
                }
            }));
            return true;
        }

        lastLeft.ifPresent(left -> sendLeft(action, left));
        lastRight.ifPresent(right -> sendRight(action, right));
        return true;
    }

    private boolean sendLeft(Consumer<? super T2<Set<L>, Set<R>>> action, Map.Entry<K, Set<L>> left) {
        action.accept(T2.of(left.getValue(), Collections.<R>emptySet()));
        return advanceLeft();
    }

    private boolean sendRight(Consumer<? super T2<Set<L>, Set<R>>> action, Map.Entry<K, Set<R>> right) {
        action.accept(T2.of(Collections.<L>emptySet(), right.getValue()));
        return advanceRight();
    }

    private boolean sendBoth(Consumer<? super T2<Set<L>, Set<R>>> action, Map.Entry<K, Set<L>> left, Map.Entry<K, Set<R>> right) {
        action.accept(T2.of(left.getValue(), right.getValue()));
        return advanceBoth();
    }

    private boolean advanceLeft() {
        lastLeft = Optional.empty();
        return leftIter.tryAdvance(left -> lastLeft = Optional.of(left));
    }

    private boolean advanceRight() {
        lastRight = Optional.empty();
        return rightIter.tryAdvance(right -> lastRight = Optional.of(right));
    }

    private boolean advanceBoth() {
        boolean leftAdvanced = advanceLeft();
        boolean rightAdvanced = advanceRight();
        return leftAdvanced || rightAdvanced;
    }

    @Override
    public Spliterator<T2<Set<L>, Set<R>>> trySplit() {
        return null;
    }

    @Override
    public long estimateSize() {
        return Long.MAX_VALUE;
    }

    @Override
    public int characteristics() {
        return Spliterator.NONNULL | Spliterator.IMMUTABLE;
    }
}
