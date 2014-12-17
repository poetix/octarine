package com.codepoetics.octarine.testutils;

import com.codepoetics.octarine.functional.paths.Path;
import com.codepoetics.octarine.api.Record;
import com.codepoetics.octarine.validation.api.Schema;
import com.codepoetics.octarine.validation.api.Validation;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;

import java.util.Optional;

import static org.hamcrest.CoreMatchers.equalTo;

public class ARecord extends TypeSafeDiagnosingMatcher<Record> {

    public static ARecord instance() {
        return new ARecord(Optional.<Schema<?>>empty());
    }

    public static <T> ARecord validAgainst(Schema<T> schema) {
        return new ARecord(Optional.of(schema));
    }

    private final Optional<Schema<?>> schema;
    private final AnInstance<Record> instanceMatcher;

    private ARecord(Optional<Schema<?>> schema) {
        this.schema = schema;
        this.instanceMatcher = AnInstance.of(Record.class);
    }

    private ARecord(Optional<Schema<?>> schema, AnInstance<Record> instanceMatcher) {
        this.schema = schema;
        this.instanceMatcher = instanceMatcher;
    }

    public <V> ARecord with(Path<Record, V> path, Matcher<? extends V> matcher) {
        return new ARecord(schema, instanceMatcher.with(path, Present.and(matcher)));
    }

    public <V> ARecord with(Path<Record, V> path, V value) {
        return new ARecord(schema, instanceMatcher.with(path, Present.and(equalTo(value))));
    }

    public <V> ARecord without(Path<Record, V> path) {
        return new ARecord(schema, instanceMatcher.with(path, equalTo(Optional.<V>empty())));
    }

    @Override
    protected boolean matchesSafely(Record record, Description description) {
        if (schema.isPresent()) {
            Validation<?> validation = schema.get().validate(record);
            if (!validation.isValid()) {
                validation.validationErrors().forEach(e -> description.appendText("\n").appendText(e));
                return false;
            }
        }
        return instanceMatcher.matchesSafely(record, description);
    }

    @Override
    public void describeTo(Description description) {
        instanceMatcher.describeTo(description);
        if (schema.isPresent()) {
            description.appendText("\nValid against: ").appendValue(schema.get());
        }
    }
}
