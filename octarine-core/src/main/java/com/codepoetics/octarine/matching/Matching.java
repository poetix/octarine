package com.codepoetics.octarine.matching;

import org.pcollections.PVector;
import org.pcollections.TreePVector;

import java.util.Optional;
import java.util.function.Function;

public interface Matching<S, T> extends Function<S, Optional<T>>, MatcherBuilder<S, T> {

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
}
