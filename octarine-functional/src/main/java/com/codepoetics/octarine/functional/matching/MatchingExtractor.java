package com.codepoetics.octarine.functional.matching;

import com.codepoetics.octarine.functional.extractors.Extractor;
import org.pcollections.PVector;
import org.pcollections.TreePVector;

import java.util.Optional;
import java.util.function.UnaryOperator;

public final class MatchingExtractor<S, T> implements Extractor.FromPartial<S, T>, MatcherBuilder<S, T> {

    static <S, T> MatchingExtractor<S, T> build(UnaryOperator<MatchingExtractor<S, T>> builder) {
        return builder.apply(new MatchingExtractor<S, T>(TreePVector.empty()));
    }

    private final PVector<Extractor<? super S, ? extends T>> criteria;

    private MatchingExtractor(PVector<Extractor<? super S, ? extends T>> criteria) {
        this.criteria = criteria;
    }

    @Override
    public MatchingExtractor<S, T> matching(Extractor<? super S, ? extends T> extractor) {
        return new MatchingExtractor<>(criteria.plus(extractor));
    }

    @Override
    public Optional<T> apply(S input) {
        for (Extractor<? super S, ? extends T> extractor : criteria) {
            if (extractor.test(input)) {
                return Optional.of(extractor.extract(input));
            }
        }
        return Optional.empty();
    }

}
