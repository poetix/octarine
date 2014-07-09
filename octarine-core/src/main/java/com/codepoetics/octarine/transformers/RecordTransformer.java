package com.codepoetics.octarine.transformers;

import com.codepoetics.octarine.extractors.Extractor;
import com.codepoetics.octarine.records.Key;
import com.codepoetics.octarine.records.Record;
import com.codepoetics.octarine.records.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

public interface RecordTransformer<T> extends Function<T, Record> {

    interface RecordTransformerConfigurator<T> {
        default <V> RecordTransformerConfigurator<T> map(Function<T, ? extends V> extractor, Key<V> key) {
            return map(t -> Optional.of(key.of(extractor.apply(t))));
        }

        default <V> RecordTransformerConfigurator<T> map(Supplier<? extends V> supplier, Key<V> key) {
            return map(t -> Optional.of(key.of(supplier.get())));
        }

        default <V> RecordTransformerConfigurator<T> map(Extractor<T, ? extends V> extractor, Key<V> key) {
            return map(t -> extractor.apply(t).map(key::of));
        }

        RecordTransformerConfigurator<T> map(Function<T, Optional<Value>> valueExtractor);
        RecordTransformerConfigurator<T> mapAll(Function<T, Stream<Value>> valueExtractor);
    }

    void configure(RecordTransformerConfigurator<T> configurator);

    default Record apply(T input) {
        List<Value> values = new ArrayList<>();
        configure(new RecordTransformerConfigurator<T>() {
            @Override
            public RecordTransformerConfigurator<T> map(Function<T, Optional<Value>> valueExtractor) {
                valueExtractor.apply(input).ifPresent(values::add);
                return this;
            }

            @Override
            public RecordTransformerConfigurator<T> mapAll(Function<T, Stream<Value>> valueExtractor) {
                valueExtractor.apply(input).forEach(values::add);
                return this;
            }
        });
        return Record.of(values);
    }
}
