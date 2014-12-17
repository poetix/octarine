package com.codepoetics.octarine.transformers;

import com.codepoetics.octarine.extractors.Extractor;
import com.codepoetics.octarine.functions.F1;
import com.codepoetics.octarine.functions.F2;
import com.codepoetics.octarine.lenses.Lens;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public interface Transformer<I, O> extends F2<O, I, O> {

    interface TransformerConfigurator<I, O> {
        default <T> TransformerConfigurator<I, O> map(Function<? super I, ? extends T> extractor, Lens<O, Optional<T>> target) {
            Function<? super I, Optional<T>> optionalFunction = extractor.andThen(Optional::ofNullable);
            return map(Extractor.from(optionalFunction), target);
        }

        <T> TransformerConfigurator<I, O> map(Extractor<? super I, ? extends T> extractor, Lens<O, Optional<T>> target);

        <T> TransformerConfigurator<I, O> set(Supplier<? extends T> supplier, Lens<O, Optional<T>> target);

        default <T> TransformerConfigurator<I, O> set(T value, Lens<O, Optional<T>> target) {
            return set((Supplier<T>) () -> value, target);
        }
    }

    void configure(TransformerConfigurator<I, O> configurator);

    default F1<I, O> seededWith(O seed) { return this.curry(seed); }

    default O apply(O seed, I input) {
        OutputBuildingTransformerConfigurator<I, O> configurator = new OutputBuildingTransformerConfigurator<>(seed, input);
        configure(configurator);
        return configurator.get();
    }

    static class OutputBuildingTransformerConfigurator<I, O> implements TransformerConfigurator<I, O>, Supplier<O> {
        private O output;
        private final I input;

        private OutputBuildingTransformerConfigurator(O seed, I input) {
            this.output = seed;
            this.input = input;
        }

        @Override
        public <T> TransformerConfigurator<I, O> map(Extractor<? super I, ? extends T> extractor, Lens<O, Optional<T>> target) {
            output = target.set(output, (Optional<T>) extractor.apply(input));
            return this;
        }

        @Override
        public <T> TransformerConfigurator<I, O> set(Supplier<? extends T> supplier, Lens<O, Optional<T>> target) {
            output = target.set(output, Optional.ofNullable(supplier.get()));
            return this;
        }

        @Override
        public O get() {
            return output;
        }
    }
}
