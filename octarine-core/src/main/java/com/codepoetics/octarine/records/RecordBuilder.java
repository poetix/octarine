package com.codepoetics.octarine.records;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

public interface RecordBuilder<T> extends BiConsumer<String, T>, Supplier<Record> { }
