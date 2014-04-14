package com.codepoetics.octarine.matchers;

import com.codepoetics.octarine.json.JsonDeserialiser;
import com.codepoetics.octarine.json.JsonSerialiser;
import com.codepoetics.octarine.records.*;
import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;
import org.junit.Test;

import static com.codepoetics.octarine.json.JsonDeserialiser.fromInteger;
import static com.codepoetics.octarine.json.JsonDeserialiser.fromString;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.number.OrderingComparison.greaterThan;

public class ARecordTest {

    public static interface Person {
        static final KeySet mandatoryKeys = new KeySet();
        static final Key<String> name = mandatoryKeys.add("name");
        static final Key<Integer> age = mandatoryKeys.add("age");

        Schema<Person> schema = (r, v) -> {
            mandatoryKeys.accept(r, v);
            age.from(r).ifPresent(a -> { if (a < 0) v.accept("Age must be 0 or greater"); });
        };

        JsonDeserialiser reader = i ->
                i.add(name, fromString)
                 .add(age, fromInteger);

        JsonSerialiser writer = p ->
            p.add(name, JsonSerialiser.asString)
             .add(age, JsonSerialiser.asInteger);
    }

    @Test public void
    matches_record_with_property_matchers() {
        Record person = Record.of(Person.name.of("Arthur Putey"), Person.age.of(42));

        assertThat(person, ARecord.validAgainst(Person.schema)
                .with(Person.name, "Arthur Putey")
                .with(Person.age, 42));
    }

    @Test public void
    applies_validations_if_schema_given() {
        Record person = Record.of(Person.name.of("Arthur Putey"), Person.age.of(-1));

        Matcher<Record> matcher = ARecord.validAgainst(Person.schema)
                .with(Person.name, "Arthur Putey")
                .with(Person.age, -1);

        StringDescription description = new StringDescription();
        matcher.describeMismatch(person, description);

        assertThat(description.toString(), containsString("Age must be 0 or greater"));
    }

    @Test public void
    lists_expected_properties_in_self_description() {
        Matcher<Record> matcher = ARecord.validAgainst(Person.schema)
                .with(Person.name, "Hubert Cumberdale")
                .with(Person.age, greaterThan(23));

        StringDescription description = new StringDescription();
        matcher.describeTo(description);

        assertThat(description.toString(), containsString("name: present and \"Hubert Cumberdale\""));
        assertThat(description.toString(), containsString("age: present and a value greater than <23>"));
    }

    @Test public void
    describes_mismatch_meaningfully() {
        Record person = Record.of(Person.name.of("Arthur Putey"));

        Matcher<Record> matcher = ARecord.instance()
                .with(Person.name, "Hubert Cumberdale")
                .without(Person.age);

        StringDescription description = new StringDescription();
        matcher.describeMismatch(person, description);

        assertThat(description.toString(), containsString("name: was \"Arthur Putey\""));
    }

    @Test public void
    deserialise_validate_update_serialise() {
        Record person = Person.reader.readFromString("{\"name\": \"Arthur Putey\", \"age\": 42}");

        assertThat(person, ARecord.validAgainst(Person.schema)
            .with(Person.name, "Arthur Putey")
            .with(Person.age, 42));

        Valid<Person> validPerson = Person.schema.validate(person).get();

        Record changed = Person.age.update(validPerson, age -> age.map(a -> a + 1));

        assertThat(Person.writer.toString(changed), equalTo("{\"name\": \"Arthur Putey\", \"age\": 43}"));
    }
}
