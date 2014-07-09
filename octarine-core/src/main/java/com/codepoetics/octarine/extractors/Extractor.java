package com.codepoetics.octarine.extractors;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

public interface Extractor<S, T> extends Predicate<S>, Function<S, Optional<T>> {

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

    static <S, T> FromOptionalFunction<S, T> from(Function<? super S, Optional<T>> f) {
        return f::apply;
    }

    T extract(S input);

    <T2> Extractor<S, T2> mappedWith(Function<? super T, ? extends T2> f);
    <T2> Extractor<S, T2> flatMappedWith(Function<? super T, Optional<T2>> f);

    default Extractor<S, T> is(T expected) {
        return is(Predicate.isEqual(expected));
    }

    default Extractor<S, T> is(Predicate<T> expected) {
        return Extractors.join(
                target -> apply(target).map(expected::test).orElse(false),
                this);
    }

    interface FromPredicate<S, T> extends Extractor<S, T> {
        @Override
        default Optional<T> apply(S input) {
            if (!test(input)) {
                return Optional.empty();
            }
            return Optional.of(extract(input));
        }

        @Override
        default <T2> Extractor<S, T2> mappedWith(Function<? super T, ? extends T2> next) {
            return from(input -> apply(input).map(next));
        }

        @Override
        default <T2> Extractor<S, T2> flatMappedWith(Function<? super T, Optional<T2>> next) {
            return from(input -> apply(input).flatMap(next));
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

        @Override
        default <T2> Extractor<S, T2> mappedWith(Function<? super T, ? extends T2> next) {
            return from(s -> apply(s).map(next));
        }

        @Override
        default <T2> Extractor<S, T2> flatMappedWith(Function<? super T, Optional<T2>> next) {
            return from(s -> apply(s).flatMap(next));
        }
    }

}
