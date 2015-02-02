package com.codepoetics.octarine.records;

public interface Value {

    <T> Key<T> key();
    <T> T value();

}
