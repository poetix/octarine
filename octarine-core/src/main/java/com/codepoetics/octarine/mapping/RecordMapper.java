package com.codepoetics.octarine.mapping;

import com.codepoetics.octarine.api.Record;

import java.util.function.Function;

@FunctionalInterface
public interface RecordMapper<T> extends Function<T, Record> {

    void configure(RecordTransformerConfigurator<T> configurator);

    default Record apply(T input) {
        RecordBuildingTransformerConfigurator<T> configurator = new RecordBuildingTransformerConfigurator<>(input);
        configure(configurator);
        return configurator.get();
    }

}
