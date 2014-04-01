package com.codepoetics.octarine.tuples;

public interface Pairable<K, V> {

    Pair<K, V> toPair();

}
