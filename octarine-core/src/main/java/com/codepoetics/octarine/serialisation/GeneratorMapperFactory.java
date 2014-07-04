package com.codepoetics.octarine.serialisation;

import com.codepoetics.octarine.records.Key;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public interface GeneratorMapperFactory<T> {

    void configure(GeneratorMappingsConfigurator<T> configurator);

    default GeneratorMapper<T> createMapper() {
        Map<Key<?>, String> keyMap = new LinkedHashMap<>();
        Map<Key<?>, BiConsumer<T, ?>> serialiserMap = new HashMap<>();

        configure(new GeneratorMappingsConfigurator<T>() {
            @SuppressWarnings("unchecked")
            @Override
            public <V> GeneratorMappingsConfigurator<T> add(Key<? extends V> key, String fieldName, BiConsumer<T, ? extends V> serialiser) {
                keyMap.put(key, fieldName);
                serialiserMap.put(key, (BiConsumer) serialiser);
                return this;
            }
    });

    return new GeneratorMapper<T>() {
        @Override
        public void generateValues(T generator, Consumer<String> fieldNameConsumer, Function<Key<?>, Object> valueGetter) {
            keyMap.keySet().forEach(k -> {
                fieldNameConsumer.accept(keyMap.get(k));
                Object value = valueGetter.apply(k);
                generateValue(k, value, generator);
            });
        }

        private <V> void generateValue(Key<? extends V> key, V value, T generator) {
            ((BiConsumer<T, V>) serialiserMap.get(key)).accept(generator, value);
        }
    };
}
}
