package com.codepoetics.octarine.keys;

import com.codepoetics.octarine.records.Record;
import org.pcollections.PVector;
import org.pcollections.TreePVector;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

public final class ListKey<T> implements Key<PVector<T>> {

    public static <T> ListKey<T> named(String name, Value... metadata) {
        return named(name, Record.of(metadata));
    }

    public static <T> ListKey<T> named(String name, Record metadata) {
        return new ListKey<>(name, metadata);
    }

    private final String name;
    private final Record metadata;

    private ListKey(String name, Record metadata) {
        this.name = name;
        this.metadata = metadata;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public Record metadata() {
        return metadata;
    }

    public Value of(Collection<? extends T> values) {
        return of(TreePVector.from(values));
    }

    @SuppressWarnings("unchecked")
    public Value of(T... values) {
        return of(TreePVector.from(Arrays.asList(values)));
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, metadata);
    }

    @Override
    public String toString() {
        return String.format("$%s (list) %s", name, metadata);
    }
}
