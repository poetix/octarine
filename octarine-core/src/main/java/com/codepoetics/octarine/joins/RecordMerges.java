package com.codepoetics.octarine.joins;

import com.codepoetics.joink.Tuple2;
import com.codepoetics.octarine.records.ListKey;
import com.codepoetics.octarine.records.Record;

import java.util.Set;
import java.util.function.Function;

public final class RecordMerges {
    private RecordMerges() { }

    public static final Function<Tuple2<Record, Record>, Record> recordIntoRecord = Tuple2.mergingWith(Record::with);

    public static Function<Tuple2<Record, Set<Record>>, Record> recordsIntoRecord(ListKey<Record> key) {
        return Tuple2.mergingWith((f, s) -> f.with(key.of(s)));
    }
}
