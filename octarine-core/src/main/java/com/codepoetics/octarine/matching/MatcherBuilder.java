package com.codepoetics.octarine.matching;

import com.codepoetics.octarine.extractors.Extractor;
import com.codepoetics.octarine.extractors.Extractors;
import com.codepoetics.octarine.functions.F3;
import com.codepoetics.octarine.functions.F4;
import com.codepoetics.octarine.functions.F5;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

public interface MatcherBuilder<S, T> {

    Matching<S, T> matching(Extractor<? super S, ? extends T> extractor);

    default Matching<S, T> matching(Function<? super S, ? extends T> receiver) {
        return matching(Extractors.it(), receiver);
    }

    default <A> Matching<S, T> matching(Extractor<? super S, ? extends A> extractor,
                                        Function<? super A, ? extends T> receiver) {
        return matching(Extractors.extend(extractor, receiver));
    }

    default MatcherBuilder<S, T> when(Predicate<? super S> predicate) {
        return new MatcherBuilder<S, T>() {
            @Override
            public Matching<S, T> matching(Extractor<? super S, ? extends T> extractor) {
                return MatcherBuilder.this.matching(
                        Extractors.join(predicate, extractor));
            }
        };
    }

    default MatcherBuilder<S, T> unless(Predicate<? super S> predicate) {
        return when(predicate.negate());
    }

    default <A, B> Matching<S, T> matching(
            Extractor<? super S, ? extends A> extractorA,
            Extractor<? super S, ? extends B> extractorB,
            BiFunction<? super A, ? super B, ? extends T> receiver) {
        return matching(Extractors.join(extractorA, extractorB, receiver));
    }

    default <A, B, C> Matching<S, T> matching(Extractor<? super S, ? extends A> extractorA,
                                              Extractor<? super S, ? extends B> extractorB,
                                              Extractor<? super S, ? extends C> extractorC,
                                              F3<? super A, ? super B, ? super C, ? extends T> receiver) {
        return matching(Extractors.join(extractorA, extractorB, extractorC, receiver));
    }

    default <A, B, C, D> Matching<S, T> matching(Extractor<? super S, ? extends A> extractorA,
                                                 Extractor<? super S, ? extends B> extractorB,
                                                 Extractor<? super S, ? extends C> extractorC,
                                                 Extractor<? super S, ? extends D> extractorD,
                                                 F4<? super A, ? super B, ? super C, ? super D, ? extends T> receiver) {
        return matching(Extractors.join(extractorA, extractorB, extractorC, extractorD, receiver));
    }

    default <A, B, C, D, E> Matching<S, T> matching(Extractor<? super S, ? extends A> extractorA,
                                                    Extractor<? super S, ? extends B> extractorB,
                                                    Extractor<? super S, ? extends C> extractorC,
                                                    Extractor<? super S, ? extends D> extractorD,
                                                    Extractor<? super S, ? extends E> extractorE,
                                                    F5<? super A, ? super B, ? super C, ? super D, ? super E, ? extends T> receiver) {
        return matching(Extractors.join(extractorA, extractorB, extractorC, extractorD, extractorE, receiver));
    }

}
