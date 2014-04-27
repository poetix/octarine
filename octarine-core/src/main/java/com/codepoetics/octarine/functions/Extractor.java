package com.codepoetics.octarine.functions;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

public interface Extractor<S, T> extends Predicate<S>, Function<S, Optional<T>> {

    T extract(S input);

    interface FromPredicate<S, T> extends Extractor<S, T> {
        @Override
        default Optional<T> apply(S input) {
            if (!test(input)) { return Optional.empty(); }
            return Optional.of(extract(input));
        }
    }

    interface FromOptionalFunction<S, T> extends Extractor<S, T> {
        @Override
        default boolean test(S input) {
            return apply(input).isPresent();
        }

        @Override
        default T extract(S input) {
            return apply(input).get();
        }
    }

    static <S> Extractor<S, S> it() {
        return from(s -> true, Function.identity());
    }

    static <S, T> Extractor<S, T> from(Predicate<? super S> predicate, Function<? super S, ? extends T> f) {
        return new FromPredicate<S, T>() {

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

    static <S, T, T2> Extractor<S, T2> extend(
            Extractor<? super S, ? extends T> extractor,
            Function<? super T, ? extends T2> f) {
        return new FromPredicate<S, T2>() {
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
        return new FromPredicate<S, J>() {
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
        return new FromPredicate<S, T>() {

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
            TetraFunction<? super A, ? super B, ? super C, ? super D, ? extends T> receiver) {
        return new FromPredicate<S, T>() {

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
        return new FromPredicate<S, T>() {
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
