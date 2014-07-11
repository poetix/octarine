package com.codepoetics.octarine.records;

import org.pcollections.HashTreePMap;
import org.pcollections.PMap;

import java.beans.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BeanRecord<B> implements Record {

    public static final Key<String> methodName = Key.named("methodName");

    public static <B> BeanRecord<B> of(B bean, Key<?>...keys) {
        try {
            return getBeanRecord(bean, Stream.of(keys).collect(Collectors.toSet()));
        } catch (IntrospectionException e) {
            throw new RuntimeException(e);
        }
    }

    private static <B> BeanRecord<B> getBeanRecord(B bean, Set<Key<?>> keys) throws IntrospectionException {
        BeanInfo beanInfo = Introspector.getBeanInfo(bean.getClass());
        PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
        Map<String, Method> readerMethods = Stream.of(propertyDescriptors).collect(Collectors.toMap(FeatureDescriptor::getDisplayName, PropertyDescriptor::getReadMethod));
        return new BeanRecord<B>(bean, readerMethods, keys);
    }

    private final B bean;
    private final Map<String, Method> readMethods;
    private final Set<Key<?>> keys;

    private BeanRecord(B bean, Map<String, Method> readMethods, Set<Key<?>> keys) {
        this.bean = bean;
        this.readMethods = readMethods;
        this.keys = keys;
    }

    @Override
    public <T> Optional<T> get(Key<T> key) {
        if (!containsKey(key)) {
            return Optional.empty();
        }

        try {
            return Optional.ofNullable((T) readMethods.get(getMethodName(key)).invoke(bean));
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private <T> String getMethodName(Key<T> key) {
        return key.metadata().get(methodName).orElse(key.name());
    }

    @Override
    public boolean containsKey(Key<?> key) {
        return keys.contains(key) && readMethods.containsKey(getMethodName(key));
    }

    @Override
    public PMap<Key<?>, Object> values() {
        return HashTreePMap.from(
                keys.stream().filter(this::containsKey).collect(Collectors.toMap(
                        Function.<Key<?>>identity(), k -> ((Key) k).extract(this))));
    }

    @Override
    public Record with(PMap<Key<?>, Object> values) {
        return Record.of(values().plusAll(values));
    }
}
