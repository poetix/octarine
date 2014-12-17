package com.codepoetics.octarine.api;

import com.codepoetics.octarine.records.HashRecord;

public interface RecordKey extends Key<Record> {

    Value of(Value... values);

}
