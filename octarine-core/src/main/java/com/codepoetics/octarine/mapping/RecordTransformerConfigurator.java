package com.codepoetics.octarine.mapping;

import com.codepoetics.octarine.records.Key;
import com.codepoetics.octarine.records.Value;
import com.codepoetics.octarine.functional.extractors.Extractor;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

public interface RecordTransformerConfigurator<T> {
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
