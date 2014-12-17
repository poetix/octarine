package com.codepoetics.octarine.mapping;

import com.codepoetics.octarine.api.Record;
import com.codepoetics.octarine.api.Value;
import com.codepoetics.octarine.records.HashRecord;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

final class RecordBuildingTransformerConfigurator<T> implements RecordTransformerConfigurator<T>, Supplier<Record> {
    private final T input;
    private final List<Value> values = new ArrayList<>();

    RecordBuildingTransformerConfigurator(T input) {
        this.input = input;
    }

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

    @Override
    public Record get() {
        return HashRecord.of(values);
    }
}
