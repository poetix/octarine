package com.codepoetics.octarine.functional.extractors;

import com.codepoetics.octarine.functional.functions.Partial;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

public interface Extractor<S, T> extends Predicate<S>, Partial<S, T> {

    static <S, T> Extractor<S, T> from(Predicate<? super S> predicate, Function<? super S, ? extends T> f) {
        return new FromPredicate<S, T>() {
            @Override
            public boolean test(S input) {
                return predicate.test(input);
            }

            @Override
            public T extract(S input) {
                if (test(input)) {
                    return f.apply(input);
                }
                throw new IllegalArgumentException(String.format("%s does not match predicate", input));
            }
        };
    }

    static <S, T> FromPartial<S, T> from(Function<? super S, Optional<T>> f) {
        return f::apply;
    }

    T extract(S input);

    default Extractor<S, T> is(T expected) {
        return is(Predicate.isEqual(expected));
    }

    default Extractor<S, T> is(Predicate<T> expected) {
        return Extractors.join(
                target -> apply(target).map(expected::test).orElse(false),
                this);
    }

    default <T2> Extractor<S, T2> mappedWith(Function<T, T2> next) {
        return from(bind(next));
    }

    default <T2> Extractor<S, T2> flatMappedWith(Function<T, Optional<T2>> next) {
        return from(bind(Partial.of(next)));
    }

    interface FromPredicate<S, T> extends Extractor<S, T> {
        @Override
        default Optional<T> apply(S input) {
            if (!test(input)) {
                return Optional.empty();
            }
            return Optional.of(extract(input));
        }
    }

    interface FromPartial<S, T> extends Extractor<S, T> {
        @Override
        default boolean test(S input) {
            return apply(input).isPresent();
        }

        @Override
        default T extract(S input) {
            return apply(input).orElseThrow(() ->
                    new IllegalArgumentException(
                            String.format("%s does not have a value for this extractor", input)));
        }

    }

}
