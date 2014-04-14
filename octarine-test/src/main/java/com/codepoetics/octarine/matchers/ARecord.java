package com.codepoetics.octarine.matchers;

import com.codepoetics.octarine.records.Record;
import com.codepoetics.octarine.records.Schema;
import org.hamcrest.TypeSafeDiagnosingMatcher;

public class ARecord<T> implements TypeSafeDiagnosingMatcher<Record> {

    public static <T> ARecord<T> against(Schema<T> schema) {
        return new ARecord<T>(schema);
    }

    private final Schema<T> schema;
    private ARecord
}
