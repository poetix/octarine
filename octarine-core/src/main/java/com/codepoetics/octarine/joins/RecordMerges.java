package com.codepoetics.octarine.joins;

import com.codepoetics.octarine.records.ListKey;
import com.codepoetics.octarine.records.Record;
import com.codepoetics.octarine.tuples.T2;

import java.util.Set;
import java.util.function.Function;

public final class RecordMerges {
    public static final Function<T2<Record, Record>, Record> recordIntoRecord = t -> t.sendTo(Record::with);

    private RecordMerges() {
    }

    public static Function<T2<Record, Set<Record>>, Record> recordsIntoRecord(ListKey<Record> key) {
        return t -> t.sendTo((f, s) -> f.with(key.of(s)));
    }
}
