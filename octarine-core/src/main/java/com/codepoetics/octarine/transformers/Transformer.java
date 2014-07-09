package com.codepoetics.octarine.transformers;

import com.codepoetics.octarine.extractors.Extractor;
import com.codepoetics.octarine.functions.F1;
import com.codepoetics.octarine.functions.F2;
import com.codepoetics.octarine.lenses.Lens;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.Supplier;

public interface Transformer<I, O> extends F2<O, I, O> {

    interface TransformerConfigurator<I, O> {
        <T> TransformerConfigurator<I, O> map(Function<? super I, ? extends T> extractor, Lens<O, Optional<T>> target);
        <T> TransformerConfigurator<I, O> map(Extractor<? super I, ? extends T> extractor, Lens<O, Optional<T>> target);
        <T> TransformerConfigurator<I, O> map(Supplier<? extends T> supplier, Lens<O, Optional<T>> target);
    }

    void configure(TransformerConfigurator<I, O> configurator);

    default F1<I, O> seededWith(O seed) { return this.curry(seed); }

    default O apply(O seed, I input) {
        AtomicReference<O> output = new AtomicReference<O>(seed);
        TransformerConfigurator<I, O> configurator = new TransformerConfigurator<I, O>() {
            @Override
            public <T> TransformerConfigurator<I, O> map(Function<? super I, ? extends T> extractor, Lens<O, Optional<T>> target) {
                output.set(target.set(output.get(), Optional.ofNullable(extractor.apply(input))));
                return this;
            }
            @Override
            public <T> TransformerConfigurator<I, O> map(Extractor<? super I, ? extends T> extractor, Lens<O, Optional<T>> target) {
                output.set(target.set(output.get(), (Optional<T>) extractor.apply(input)));
                return this;
            }

            @Override
            public <T> TransformerConfigurator<I, O> map(Supplier<? extends T> supplier, Lens<O, Optional<T>> target) {
                output.set(target.set(output.get(), Optional.ofNullable(supplier.get())));
                return this;
            }
        };
        configure(configurator);
        return output.get();
    }
}
