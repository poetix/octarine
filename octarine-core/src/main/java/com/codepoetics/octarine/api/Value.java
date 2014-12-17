package com.codepoetics.octarine.api;

public interface Value {

    <T> Key<T> key();
    <T> T value();

}
