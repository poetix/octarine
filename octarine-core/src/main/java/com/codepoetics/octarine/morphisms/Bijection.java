package com.codepoetics.octarine.morphisms;

import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public interface Bijection<A, B> {

    static <A, B> Bijection<A, B> of(Function<A, B> in, Function<B, A> out) {
        return new Bijection<A, B>() {

            @Override
            public B in(A value) {
                return in.apply(value);
            }

            @Override
            public A out(B value) {
                return out.apply(value);
            }
        };
    }

    static Bijection<String, Stream<Character>> stringToCharacterStream = of(
        s -> IntStream.range(0, s.length()).mapToObj(i -> s.charAt(i)),
        ss -> {
            StringBuilder sb = new StringBuilder();
            ss.forEach(sb::append);
            return sb.toString();
        }
    );

    static <T> Bijection<Stream<T>, T[]> streamToArray() {
        return of(
                s -> (T[]) s.toArray(),
                Arrays::stream
        );
    }

    static final Bijection<String, Character[]> stringToCharacters = stringToCharacterStream
            .andThen(Bijection.streamToArray());

    B in(A value);
    A out(B value);

    default Bijection<B, A> reverse() {
        return new Bijection<B, A>() {

            @Override
            public A in(B value) {
                return Bijection.this.out(value);
            }

            @Override
            public B out(A value) {
                return Bijection.this.in(value);
            }
        };
    }

    default <C> Bijection<A, C> andThen(Bijection<B, C> next) {
         return new Bijection<A, C>() {

             @Override
             public C in(A value) {
                 return next.in(Bijection.this.in(value));
             }

             @Override
             public A out(C value) {
                 return Bijection.this.out(next.out(value));
             }
         };
    }

    default <C> Bijection<C, B> compose(Bijection<C, A> previous) {
        return previous.andThen(this);
    }

    default Function<A, A> translate(Function<B, B> f) {
        return a -> out(f.apply(in(a)));
    }
}
