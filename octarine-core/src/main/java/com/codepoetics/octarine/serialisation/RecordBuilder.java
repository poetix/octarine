package com.codepoetics.octarine.serialisation;

import com.codepoetics.octarine.records.Record;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

public interface RecordBuilder<T> extends BiConsumer<String, T>, Supplier<Record> { }
