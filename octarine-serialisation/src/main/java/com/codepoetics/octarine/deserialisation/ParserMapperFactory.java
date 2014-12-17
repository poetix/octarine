package com.codepoetics.octarine.deserialisation;

import com.codepoetics.octarine.api.Key;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public interface ParserMapperFactory<T> {

    void configure(ParserMappingsConfigurator<T> configurator);

    default public ParserMapper<T> createMapper() {
        Map<String, Key<?>> keyMap = new HashMap<>();
        Map<String, Function<T, ?>> deserialiserMap = new HashMap<>();

        configure(new ParserMappingsConfigurator<T>() {
            @SuppressWarnings("unchecked")
            @Override
            public <V> ParserMappingsConfigurator<T> add(Key<? extends V> key, String fieldName, Function<T, ? extends V> deserialiser) {
                keyMap.put(fieldName, key);
                deserialiserMap.put(fieldName, (Function) deserialiser);
                return this;
            }
        });

        return (fieldName, parser) -> Optional.ofNullable(keyMap.get(fieldName)).map((Key key) ->
            key.of(deserialiserMap.get(fieldName).apply(parser))
        );
    }
}
