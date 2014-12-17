package com.codepoetics.octarine.api;

public interface RecordKey extends Key<Record> {

    Value of(Value... values);

}
