package com.codepoetics.octarine.records;

import com.codepoetics.octarine.keys.Key;
import org.pcollections.HashTreePMap;
import org.pcollections.PMap;

import java.beans.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class BeanRecord<B> implements Record {

    public static final Key<String> methodName = Key.named("methodName");

    public static <B> BeanRecord<B> of(B bean, Key<?>...keys) {
        try {
            return getBeanRecord(bean, Stream.of(keys).collect(Collectors.toSet()));
        } catch (IntrospectionException e) {
            throw new RuntimeException(e);
        }
    }

    private static ConcurrentMap<Class<?>, Map<String, Method>> readMethodMapCache = new ConcurrentHashMap<>();

    private static <B> BeanRecord<B> getBeanRecord(B bean, Set<Key<?>> keys) throws IntrospectionException {
        Map<String, Method> readerMethods = readMethodMapCache.computeIfAbsent(bean.getClass(), BeanRecord::getReaderMethods);

        Map<Key<?>, String> methodNames = keys.stream().collect(Collectors.toMap(
                k -> k,
                k -> methodName.get(k.metadata()).orElseGet(k::name)
        ));

        Map<Key<?>, Method> keyedMethods = keys.stream()
            .filter(k -> readerMethods.containsKey(methodNames.get(k)))
            .collect(Collectors.toMap(
                    k -> k,
                    k -> readerMethods.get(methodNames.get(k))
            ));

        return new BeanRecord<B>(bean, keyedMethods);
    }

    private static <B> Map<String, Method> getReaderMethods(Class<B> beanClass) {
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(beanClass);
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            return Stream.of(propertyDescriptors)
                    .collect(Collectors.toMap(
                            FeatureDescriptor::getDisplayName,
                            PropertyDescriptor::getReadMethod));
        } catch (IntrospectionException e) {
            throw new RuntimeException(e);
        }
    }

    private final B bean;
    private final Map<Key<?>, Method> keyedMethods;

    private BeanRecord(B bean, Map<Key<?>, Method> keyedMethods) {
        this.bean = bean;
        this.keyedMethods = keyedMethods;
    }

    @Override
    public <T> Optional<T> get(Key<T> key) {
        if (!containsKey(key)) {
            return Optional.empty();
        }

        return getReflectively(key);
    }

    private <T> Optional<T> getReflectively(Key<T> key) {
        try {
            return Optional.ofNullable((T) keyedMethods.get(key).invoke(bean));
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean containsKey(Key<?> key) {
        return keyedMethods.containsKey(key);
    }

    @Override
    public PMap<Key<?>, Object> values() {
        Map<Key<?>, Optional<?>> values = keyedMethods.keySet().stream()
                .collect(Collectors.toMap(
                        k -> k,
                        this::getReflectively));

        Map<Key<?>, Object> nonNullValues = values.entrySet().stream()
                        .filter(e -> e.getValue().isPresent())
                        .collect(Collectors.toMap(
                                Map.Entry::getKey,
                                e -> e.getValue().get()
                        ));

        return HashTreePMap.from(nonNullValues);
    }

    @Override
    public Record with(PMap<Key<?>, Object> values) {
        return Record.of(values().plusAll(values));
    }
}
