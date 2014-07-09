package com.codepoetics.octarine.extractors;

import com.codepoetics.octarine.functions.F3;
import com.codepoetics.octarine.functions.F4;
import com.codepoetics.octarine.functions.F5;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

public class Extractors {
    private Extractors() {
    }

    public static <S> Extractor<S, S> it() {
        return Extractor.from(s -> true, Function.identity());
    }

    public static <S, A, B, J> Extractor<S, J> join(
            Extractor<? super S, ? extends A> first,
            Extractor<? super S, ? extends B> second,
            BiFunction<? super A, ? super B, ? extends J> joiner) {
        return Extractor.from(
                input -> first.test(input) && second.test(input),
                input -> joiner.apply(first.extract(input), second.extract(input)));
    }

    public static <S, A, B, C, T> Extractor<S, T> join(
            Extractor<? super S, ? extends A> extractorA,
            Extractor<? super S, ? extends B> extractorB,
            Extractor<? super S, ? extends C> extractorC,
            F3<? super A, ? super B, ? super C, ? extends T> receiver) {
        return Extractor.from(
                input -> extractorA.test(input) && extractorB.test(input) && extractorC.test(input),
                input -> receiver.apply(extractorA.extract(input), extractorB.extract(input), extractorC.extract(input)));
    }

    public static <S, A, B, C, D, T> Extractor<S, T> join(
            Extractor<? super S, ? extends A> extractorA,
            Extractor<? super S, ? extends B> extractorB,
            Extractor<? super S, ? extends C> extractorC,
            Extractor<? super S, ? extends D> extractorD,
            F4<? super A, ? super B, ? super C, ? super D, ? extends T> receiver) {
            return Extractor.from(
                    input -> extractorA.test(input) && extractorB.test(input) && extractorC.test(input) && extractorD.test(input),
                    input -> receiver.apply(extractorA.extract(input), extractorB.extract(input), extractorC.extract(input), extractorD.extract(input)));
    }


    public static <S, A, B, C, D, E, T> Extractor<S, T> join(
            Extractor<? super S, ? extends A> extractorA,
            Extractor<? super S, ? extends B> extractorB,
            Extractor<? super S, ? extends C> extractorC,
            Extractor<? super S, ? extends D> extractorD,
            Extractor<? super S, ? extends E> extractorE,
            F5<? super A, ? super B, ? super C, ? super D, ? super E, ? extends T> receiver) {
        return Extractor.from(
                input -> extractorA.test(input) && extractorB.test(input) && extractorC.test(input) && extractorD.test(input) && extractorE.test(input),
                input -> receiver.apply(extractorA.extract(input), extractorB.extract(input), extractorC.extract(input), extractorD.extract(input), extractorE.extract(input)));
    }

    public static <S, T> Extractor<S, T> join(
            Predicate<? super S> predicate,
            Extractor<? super S, ? extends T> extractor) {
        return Extractor.from(
                input -> predicate.test(input) && extractor.test(input),
                input -> extractor.extract(input));
    }
}
