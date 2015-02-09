package com.codepoetics.octarine.joins;

import com.codepoetics.octarine.functional.tuples.T2;

import java.util.*;
import java.util.function.Consumer;

class KeyMatchingSpliterator<K, L, R> implements Spliterator<T2<L, R>> {

    public static <K, L, R> KeyMatchingSpliterator<K, L, R> over(
            Comparator<? super K> comparator,
            Spliterator<Map.Entry<K, L>> self,
            Spliterator<Map.Entry<K, R>> other,
            L emptyLeft,
            R emptyRight) {
        return new KeyMatchingSpliterator<>(comparator, self, other, emptyLeft, emptyRight);
    }

    private final Comparator<? super K> comparator;
    private final Spliterator<Map.Entry<K, L>> leftIter;
    private final Spliterator<Map.Entry<K, R>> rightIter;
    private final L emptyLeft;
    private final R emptyRight;

    private boolean initialised = false;
    private Map.Entry<K, L> leftBuffer = null;
    private Map.Entry<K, R> rightBuffer = null;

    private boolean hasLeft = true;
    private boolean hasRight = true;

    KeyMatchingSpliterator(Comparator<? super K> comparator,
                           Spliterator<Map.Entry<K, L>> leftIter,
                           Spliterator<Map.Entry<K, R>> rightIter,
                           L emptyLeft,
                           R emptyRight) {
        this.comparator = comparator;
        this.leftIter = leftIter;
        this.rightIter = rightIter;
        this.emptyLeft = emptyLeft;
        this.emptyRight = emptyRight;
    }

    private void setLeftBuffer(Map.Entry<K, L> leftBuffer) {
        this.leftBuffer = leftBuffer;
    }

    private void setRightBuffer(Map.Entry<K, R> rightBuffer) {
        this.rightBuffer = rightBuffer;
    }

    @Override
    public boolean tryAdvance(Consumer<? super T2<L, R>> action) {
        if (!initialised) {
            advanceBoth();
            initialised = true;
        }

        if (!hasLeft && !hasRight) {
            return false;
        }

        if (hasLeft && hasRight) {
            sendGreatest(action);
        } else if (hasLeft) {
            sendLeft(action);
        } else {
            sendRight(action);
        }

        return true;
    }

    private void sendGreatest(Consumer<? super T2<L, R>> action) {
        int cmp = comparator.compare(leftBuffer.getKey(), rightBuffer.getKey());

        if (cmp < 0) {
            sendLeft(action);
        }

        if (cmp > 0) {
            sendRight(action);
        }

        if (cmp == 0) {
            sendBoth(action);
        }
    }

    private void sendLeft(Consumer<? super T2<L, R>> action) {
        action.accept(T2.of(leftBuffer.getValue(), emptyRight));
        advanceLeft();
    }

    private void sendRight(Consumer<? super T2<L, R>> action) {
        action.accept(T2.of(emptyLeft, rightBuffer.getValue()));
        advanceRight();
    }

    private void sendBoth(Consumer<? super T2<L, R>> action) {
        action.accept(T2.of(leftBuffer.getValue(), rightBuffer.getValue()));
        advanceBoth();
    }

    private void advanceLeft() {
        hasLeft = leftIter.tryAdvance(this::setLeftBuffer);
    }

    private void advanceRight() {
        hasRight = rightIter.tryAdvance(this::setRightBuffer);
    }

    private void advanceBoth() {
        advanceLeft();
        advanceRight();
    }

    @Override
    public Spliterator<T2<L, R>> trySplit() {
        return null;
    }

    @Override
    public long estimateSize() {
        return Long.MAX_VALUE;
    }

    @Override
    public int characteristics() {
        return Spliterator.ORDERED | Spliterator.NONNULL | Spliterator.IMMUTABLE;
    }
}
