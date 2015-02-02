package com.codepoetics.octarine.matchers;

import com.codepoetics.octarine.records.Key;
import com.codepoetics.octarine.records.ListKey;
import com.codepoetics.octarine.records.Record;
import com.codepoetics.octarine.records.RecordKey;
import com.codepoetics.octarine.functional.paths.Path;
import com.codepoetics.octarine.json.deserialisation.RecordDeserialiser;
import com.codepoetics.octarine.json.serialisation.RecordSerialiser;
import com.codepoetics.octarine.records.KeySet;
import com.codepoetics.octarine.records.Schema;
import com.codepoetics.octarine.records.Valid;
import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;
import org.junit.Test;

import static com.codepoetics.octarine.Octarine.*;
import static com.codepoetics.octarine.json.deserialisation.Deserialisers.*;
import static com.codepoetics.octarine.json.serialisation.Serialisers.*;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.number.OrderingComparison.greaterThan;

public class ARecordTest {

    public static interface Address {
        static final KeySet mandatoryKeys = new KeySet();
        static final ListKey<String> addressLines = mandatoryKeys.add($L("addressLines"));
        static final Key<String> postcode = mandatoryKeys.add($("postcode"));

        Schema<Address> schema = mandatoryKeys::accept;

        RecordDeserialiser reader = RecordDeserialiser.builder()
                .readList(addressLines, ofString)
                .readString(postcode)
                .get();

        RecordSerialiser writer = RecordSerialiser.builder()
                .writeList(addressLines, toString)
                .writeString(postcode)
                .get();
    }

    public static interface Person {
        static final KeySet mandatoryKeys = new KeySet();
        static final Key<String> name = mandatoryKeys.add($("name"));
        static final Key<Integer> age = mandatoryKeys.add($("age"));
        static final RecordKey address = mandatoryKeys.add($R("address"));

        Schema<Person> schema = (r, v) -> {
            mandatoryKeys.accept(r, v);
            age.apply(r).ifPresent(a -> { if (a < 0) v.accept("Age must be 0 or greater"); });
            address.apply(r).ifPresent(a -> Address.schema.accept(a, v));
        };

        RecordDeserialiser reader = RecordDeserialiser.builder()
                .readString(name)
                .readInteger(age)
                .read(address, Address.reader)
                .get();

        RecordSerialiser writer = RecordSerialiser.builder()
            .write(name, toString)
            .write(age, toInteger)
            .write(address, Address.writer)
            .get();
    }

    private final Record person = $$(
            Person.name.of("Arthur Putey"),
            Person.age.of(42),
            Person.address.of(
                    Address.addressLines.of("62 Acacia Avenue", "Sunderland"),
                    Address.postcode.of("VB6 5UX")));


    @Test public void
    matches_record_with_property_matchers() {
        assertThat(person, ARecord.validAgainst(Person.schema)
                .with(Person.name, "Arthur Putey")
                .with(Person.age, 42)
                // Chaining keys
                .with(Person.address.join(Address.addressLines).join(Path.toIndex(0)), "62 Acacia Avenue")
                // Using a sub-matcher
                .with(Person.address, ARecord.validAgainst(Address.schema).with(Address.postcode, "VB6 5UX")));
    }

    @Test public void
    applies_validations_if_schema_given() {
        Record invalidPerson = person.with(Person.age.of(-1));

        Matcher<Record> matcher = ARecord.validAgainst(Person.schema)
                .with(Person.name, "Arthur Putey")
                .with(Person.age, -1);

        StringDescription description = new StringDescription();
        matcher.describeMismatch(invalidPerson, description);

        assertThat(description.toString(), containsString("Age must be 0 or greater"));
    }

    @Test public void
    lists_expected_properties_in_self_description() {
        Matcher<Record> matcher = ARecord.validAgainst(Person.schema)
                .with(Person.name, "Hubert Cumberdale")
                .with(Person.age, greaterThan(23))
                .with(Person.address.join(Address.postcode), "HR9 5BH");

        StringDescription description = new StringDescription();
        matcher.describeTo(description);

        assertThat(description.toString(), containsString("name: present and \"Hubert Cumberdale\""));
        assertThat(description.toString(), containsString("age: present and a value greater than <23>"));
        assertThat(description.toString(), containsString("address.postcode: present and \"HR9 5BH\""));
    }

    @Test public void
    describes_mismatch_meaningfully() {
        Matcher<Record> matcher = ARecord.instance()
                .with(Person.name, "Hubert Cumberdale")
                .without(Person.age);

        StringDescription description = new StringDescription();
        matcher.describeMismatch(person, description);

        assertThat(description.toString(), containsString("name: was \"Arthur Putey\""));
        assertThat(description.toString(), containsString("age: was <Optional[42]>"));
    }

    @Test public void
    deserialise_validate_update_serialise() {
        Record record = Person.reader.fromString(
                "{\"name\": \"Arthur Putey\",\n" + "" +
                        " \"age\": 42,\n" +
                        " \"address\": {\n" +
                        "   \"addressLines\": [\"59 Broad Street\", \"Cirencester\"],\n" +
                        "   \"postcode\": \"RA8 81T\"\n" +
                        "  }\n" +
                        "}");

        assertThat(record, ARecord.validAgainst(Person.schema)
                .with(Person.name, "Arthur Putey")
                .with(Person.age, 42)
                        // Chaining keys
                .with(Person.address.join(Address.addressLines).join(Path.toIndex(0)), "59 Broad Street")
                        // Using a sub-matcher
                .with(Person.address, ARecord.validAgainst(Address.schema).with(Address.postcode, "RA8 81T")));


        Valid<Person> validRecord = Person.schema.validate(record).get();

        Record changed = Person.age.update(validRecord, age -> age.map(a -> a + 1));

        assertThat(Person.writer.toString(changed), equalTo(
                "{\"name\":\"Arthur Putey\"," +"" +
                        "\"age\":43," +
                        "\"address\":{" +
                        "\"addressLines\":[\"59 Broad Street\",\"Cirencester\"]," +
                        "\"postcode\":\"RA8 81T\"" +
                        "}}"));
    }
}
