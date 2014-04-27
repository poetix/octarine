package com.codepoetics.octarine.matching;

import com.codepoetics.octarine.functions.QuadFunction;
import com.codepoetics.octarine.functions.TriFunction;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

public interface Extractor<S, T> extends Predicate<S> {

    T extract(S input);

    static <S> Extractor<S, S> it() {
        return from(s -> true, Function.identity());
    }

    static <S, T> Extractor<S, T> from(Predicate<? super S> predicate, Function<? super S, ? extends T> f) {
        return new Extractor<S, T>() {

            @Override
            public boolean test(S input) {
                return predicate.test(input);
            }

            @Override
            public T extract(S input) {
                return f.apply(input);
            }
        };
    }

    default Optional<T> tryExtract(S input) {
        if (!test(input)) { return Optional.empty(); }
        return Optional.of(extract(input));
    }

    static <S, T, T2> Extractor<S, T2> extend(
            Extractor<? super S, ? extends T> extractor,
            Function<? super T, ? extends T2> f) {
        return new Extractor<S, T2>() {
            @Override
            public boolean test(S input) {
                return extractor.test(input);
            }

            @Override
            public T2 extract(S input) {
                return f.apply(extractor.extract(input));
            }
        };
    }


    static <S, A, B, J> Extractor<S, J> join(
            Extractor<? super S, ? extends A> first,
            Extractor<? super S, ? extends B> second,
            BiFunction<? super A, ? super B, ? extends J> joiner) {
        return new Extractor<S, J>() {
            @Override
            public boolean test(S input) {
                return first.test(input) && second.test(input);
            }

            @Override
            public J extract(S input) {
                return joiner.apply(first.extract(input), second.extract(input));
            }
        };
    }

    static <S, A, B, C, T> Extractor<S, T> join(
            Extractor<? super S, ? extends A> extractorA,
            Extractor<? super S, ? extends B> extractorB,
            Extractor<? super S, ? extends C> extractorC,
            TriFunction<? super A, ? super B, ? super C, ? extends T> receiver) {
        return new Extractor<S, T>() {

            @Override
            public boolean test(S input) {
                return extractorA.test(input) && extractorB.test(input) && extractorC.test(input);
            }

            @Override
            public T extract(S input) {
                return receiver.apply(extractorA.extract(input), extractorB.extract(input), extractorC.extract(input));
            }
        };
    }

    static <S, A, B, C, D, T> Extractor<S, T> join(
            Extractor<? super S, ? extends A> extractorA,
            Extractor<? super S, ? extends B> extractorB,
            Extractor<? super S, ? extends C> extractorC,
            Extractor<? super S, ? extends D> extractorD,
            QuadFunction<? super A, ? super B, ? super C, ? super D, ? extends T> receiver) {
        return new Extractor<S, T>() {

            @Override
            public boolean test(S input) {
                return extractorA.test(input) && extractorB.test(input) && extractorC.test(input) && extractorD.test(input);
            }

            @Override
            public T extract(S input) {
                return receiver.apply(extractorA.extract(input), extractorB.extract(input), extractorC.extract(input), extractorD.extract(input));
            }
        };
    }

    static <S, T> Extractor<S, T> join(
            Predicate<? super S> predicate,
            Extractor<? super S, ? extends T> extractor) {
        return new Extractor<S, T>() {
            @Override
            public boolean test(S input) {
                return predicate.test(input) && extractor.test(input);
            }

            @Override
            public T extract(S input) {
                return extractor.extract(input);
            }
        };
    }

}
