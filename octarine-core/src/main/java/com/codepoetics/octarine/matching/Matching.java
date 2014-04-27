package com.codepoetics.octarine.matching;

import com.codepoetics.octarine.functions.QuadFunction;
import com.codepoetics.octarine.functions.TriFunction;
import org.pcollections.PVector;
import org.pcollections.TreePVector;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

public interface Matching<S, T> extends Function<S, Optional<T>> {

    static <S, T> Matching<S, T> build(Function<Matching<S, T>, Matching<S, T>> builder) {
        return builder.apply(Matching.empty());
    }

    static <S, T> Matching<S, T> empty() {
        PVector<Extractor<? super S, ? extends T>> criteria = TreePVector.empty();
        return withCriteria(criteria);
    }

    static <S, T> Matching<S, T> withCriteria(PVector<Extractor<? super S, ? extends T>> criteria) {
        return new Matching<S, T>() {
            @Override public Matching<S, T> matching(Extractor<? super S, ? extends T> extractor) {
                return withCriteria(criteria.plus(extractor));
            }

            @Override public Optional<T> apply(S input) {
                for (Extractor<? super S, ? extends T> extractor : criteria) {
                    if (extractor.test(input)) { return Optional.of(extractor.extract(input)); }
                }
                return Optional.empty();
            }
        };
    }

    Matching<S, T> matching(Extractor<? super S, ? extends T> extractor);

    default <A> Matching<S, T> matching(Extractor<? super S, ? extends A> extractor,
                                        Function<? super A, ? extends T> receiver) {
        return matching(Extractor.extend(extractor, receiver));
    }

    default <A> Matching<S, T> matching(Predicate<? super S> predicate,
                                        Extractor<? super S, ? extends A> extractor,
                                        Function<? super A, ? extends T> receiver) {
        return matching(Extractor.join(predicate, Extractor.<S, A, T>extend(extractor, receiver)));
    }

    default <A, B> Matching<S, T> matching(
            Extractor<? super S, ? extends A> extractorA,
            Extractor<? super S, ? extends B> extractorB,
            BiFunction<? super A, ? super B, ? extends T> receiver) {
        return matching(Extractor.join(extractorA, extractorB, receiver));
    }

    default <A, B> Matching<S, T> matching(
            Predicate<? super S> predicate,
            Extractor<? super S, ? extends A> extractorA,
            Extractor<? super S, ? extends B> extractorB,
            BiFunction<? super A, ? super B, ? extends T> receiver) {
        return matching(Extractor.join(predicate, Extractor.join(extractorA, extractorB, receiver)));
    }

    default <A, B, C> Matching<S, T> matching(Extractor<? super S, ? extends A> extractorA,
                                              Extractor<? super S, ? extends B> extractorB,
                                              Extractor<? super S, ? extends C> extractorC,
                                              TriFunction<? super A, ? super B, ? super C, ? extends T> receiver) {
        return matching(Extractor.join(extractorA, extractorB, extractorC, receiver));
    }

    default <A, B, C> Matching<S, T> matching(Predicate<? super S> predicate,
                                              Extractor<? super S, ? extends A> extractorA,
                                              Extractor<? super S, ? extends B> extractorB,
                                              Extractor<? super S, ? extends C> extractorC,
                                              TriFunction<? super A, ? super B, ? super C, ? extends T> receiver) {
        return matching(Extractor.join(predicate, Extractor.join(extractorA, extractorB, extractorC, receiver)));
    }

    default <A, B, C, D> Matching<S, T> matching(Extractor<? super S, ? extends A> extractorA,
                                              Extractor<? super S, ? extends B> extractorB,
                                              Extractor<? super S, ? extends C> extractorC,
                                              Extractor<? super S, ? extends D> extractorD,
                                              QuadFunction<? super A, ? super B, ? super C, ? super D, ? extends T> receiver) {
        return matching(Extractor.join(extractorA, extractorB, extractorC, extractorD, receiver));
    }

    default <A, B, C, D> Matching<S, T> matching(Predicate<? super S> predicate,
                                              Extractor<? super S, ? extends A> extractorA,
                                              Extractor<? super S, ? extends B> extractorB,
                                              Extractor<? super S, ? extends C> extractorC,
                                              Extractor<? super S, ? extends D> extractorD,
                                              QuadFunction<? super A, ? super B, ? super C, ? super D, ? extends T> receiver) {
        return matching(Extractor.join(predicate, Extractor.join(extractorA, extractorB, extractorC, extractorD, receiver)));
    }

}
