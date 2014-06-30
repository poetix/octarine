package com.codepoetics.octarine.deserialisation;

import com.codepoetics.octarine.records.Key;
import com.codepoetics.octarine.records.Value;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public interface ParserMapperFactory<T> {

    void configure(ParserMappingsConfigurator<T> configurator);

    default public ParserMapper<T> createMapper() {
        Map<String, Key<?>> keyMap = new HashMap<>();
        Map<String, Function<T, ?>> deserialiserMap = new HashMap<>();
        configure(new ParserMappingsConfigurator<T>() {
            @Override
            public <V> ParserMappingsConfigurator<T> add(Key<? extends V> key, String fieldName, Function<T, ? extends V> deserialiser) {
                keyMap.put(fieldName, key);
                deserialiserMap.put(fieldName, (Function) deserialiser);
                return this;
            }
        });

        return new ParserMapper<T>() {
            @Override
            public boolean hasKeyFor(String fieldName) {
                return keyMap.containsKey(fieldName);
            }

            @Override
            public Value getValue(String fieldName, T parser) {
                Key key = keyMap.get(fieldName);
                Function<T, ?> deserialiser = deserialiserMap.get(fieldName);
                return key.of(deserialiser.apply(parser));
            }
        };
    }
}
