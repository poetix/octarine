package com.codepoetics.octarine.functional.matching;

import com.codepoetics.octarine.functional.extractors.Extractor;
import com.codepoetics.octarine.functional.extractors.Extractors;
import com.codepoetics.octarine.functional.functions.F3;
import com.codepoetics.octarine.functional.functions.F4;
import com.codepoetics.octarine.functional.functions.F5;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

@FunctionalInterface
public interface MatcherBuilder<S, T> {

    MatchingExtractor<S, T> matching(Extractor<? super S, ? extends T> extractor);

    default MatchingExtractor<S, T> matching(Function<? super S, ? extends T> receiver) {
        return matching(Extractors.it(), receiver);
    }

    default <A> MatchingExtractor<S, T> matching(Extractor<? super S, ? extends A> extractor,
                                        Function<? super A, ? extends T> receiver) {
        return matching(extractor.mappedWith(receiver::apply));
    }

    default MatcherBuilder<S, T> when(Predicate<? super S> predicate) {
        return extractor -> MatcherBuilder.this.matching(
                Extractors.join(predicate, extractor));
    }

    default MatcherBuilder<S, T> unless(Predicate<? super S> predicate) {
        return when(predicate.negate());
    }

    default <A, B> MatchingExtractor<S, T> matching(
            Extractor<? super S, ? extends A> extractorA,
            Extractor<? super S, ? extends B> extractorB,
            BiFunction<? super A, ? super B, ? extends T> receiver) {
        return matching(Extractors.join(extractorA, extractorB, receiver));
    }

    default <A, B, C> MatchingExtractor<S, T> matching(Extractor<? super S, ? extends A> extractorA,
                                              Extractor<? super S, ? extends B> extractorB,
                                              Extractor<? super S, ? extends C> extractorC,
                                              F3<? super A, ? super B, ? super C, ? extends T> receiver) {
        return matching(Extractors.join(extractorA, extractorB, extractorC, receiver));
    }

    default <A, B, C, D> MatchingExtractor<S, T> matching(Extractor<? super S, ? extends A> extractorA,
                                                 Extractor<? super S, ? extends B> extractorB,
                                                 Extractor<? super S, ? extends C> extractorC,
                                                 Extractor<? super S, ? extends D> extractorD,
                                                 F4<? super A, ? super B, ? super C, ? super D, ? extends T> receiver) {
        return matching(Extractors.join(extractorA, extractorB, extractorC, extractorD, receiver));
    }

    default <A, B, C, D, E> MatchingExtractor<S, T> matching(Extractor<? super S, ? extends A> extractorA,
                                                    Extractor<? super S, ? extends B> extractorB,
                                                    Extractor<? super S, ? extends C> extractorC,
                                                    Extractor<? super S, ? extends D> extractorD,
                                                    Extractor<? super S, ? extends E> extractorE,
                                                    F5<? super A, ? super B, ? super C, ? super D, ? super E, ? extends T> receiver) {
        return matching(Extractors.join(extractorA, extractorB, extractorC, extractorD, extractorE, receiver));
    }

}
