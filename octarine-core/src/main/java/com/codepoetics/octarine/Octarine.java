package com.codepoetics.octarine;

import com.codepoetics.octarine.keys.*;
import com.codepoetics.octarine.records.*;

import java.util.stream.Stream;

public final class Octarine {
    private Octarine() { }

    public static <T> Key<T> $(String name, Value...metadata) {
        return Key.named(name, metadata);
    }

    public static <T> Key<T> $(String name, Record metadata) {
        return Key.named(name, metadata);
    }

    public static <T> ListKey<T> $L(String name, Value...metadata) {
        return ListKey.named(name, metadata);
    }

    public static <T> ListKey<T> $L(String name, Record metadata) {
        return ListKey.named(name, metadata);
    }

    public static RecordKey $R(String name, Value...metadata) {
        return RecordKey.named(name, metadata);
    }

    public static RecordKey $R(String name, Record metadata) {
        return RecordKey.named(name, metadata);
    }

    public static <T> MapKey<T> $M(String name, Value...metadata) {
        return MapKey.named(name, metadata);
    }

    public static <T> MapKey<T> $M(String name, Record metadata) {
        return MapKey.named(name, metadata);
    }

    public static Record $$(Value...values) { return Record.of(values); }
    public static Record $$(Record source, Value...values) { return source.with(values); }
    public static Record $$(Record...records) {
        return Stream.of(records).reduce(Record.empty(), Record::with);
    }

}
