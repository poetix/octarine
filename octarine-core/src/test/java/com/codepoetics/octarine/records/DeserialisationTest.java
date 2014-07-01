package com.codepoetics.octarine.records;

import com.codepoetics.octarine.json.JsonRecordDeserialiser;
import com.codepoetics.octarine.paths.Path;
import com.codepoetics.octarine.records.example.Address;
import com.codepoetics.octarine.records.example.Person;
import com.codepoetics.octarine.validation.Valid;
import com.codepoetics.octarine.validation.Validation;
import org.junit.Test;
import org.pcollections.PMap;
import org.pcollections.PVector;
import org.pcollections.TreePVector;

import java.awt.*;
import java.util.Map;

import static com.codepoetics.octarine.json.JsonDeserialisers.*;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

public class DeserialisationTest {

    private static final Key<String> prefix = Key.named("prefix");
    private static final Key<String> number = Key.named("number");
    private static final JsonRecordDeserialiser readNumber = c ->
            c.add(prefix, fromString)
                    .add(number, fromString);
    private static final Key<PMap<String, Record>> numbers = Key.named("numbers");
    private static final JsonRecordDeserialiser readNumbers = c ->
            c.add(numbers, fromMap(readNumber));

    @Test
    public void
    deserialises_json_to_record() {
        Valid<Person> person = Person.deserialiser.validAgainst(Person.schema).fromString(String.join("\n",
                        "{",
                        "    \"name\": \"Dominic\",",
                        "    \"age\": 39,",
                        "    \"favourite colour\": \"0xFF0000\",",
                        "    \"address\": {",
                        "        \"addressLines\": [",
                        "            \"13 Rue Morgue\",",
                        "            \"PO3 1TP\"",
                        "        ]",
                        "    }",
                        "}")
        ).get();

        assertThat(Person.name.extract(person), equalTo("Dominic"));
        assertThat(Person.age.extract(person), equalTo(39));
        assertThat(Person.favouriteColour.extract(person), equalTo(Color.RED));
        assertThat(Person.address.join(Address.addressLines).join(Path.toIndex(1))
                        .extract(person),
                equalTo("PO3 1TP")
        );
    }

    @Test
    public void
    returns_nested_validation_exceptions() {
        Validation<Person> personResult = Person.deserialiser.validAgainst(Person.schema).fromString(String.join("\n",
                "{",
                "    \"name\": \"Dominic\",",
                "    \"age\": 39,",
                "    \"favourite colour\": \"0xFF0000\",",
                "    \"address\": {",
                "        \"addressLinez\": [",
                "            \"13 Rue Morgue\",",
                "            \"PO3 1TP\"",
                "        ]",
                "    }",
                "}"));

        assertThat(personResult.isValid(), equalTo(false));
        assertThat(personResult.validationErrors(), hasItem(containsString("addressLines")));
    }

    @Test
    public void
    handles_empty_arrays() {
        assertThat(Address.deserialiser.fromString("{\"addressLines\":[]}").get(Address.addressLines).get().size(), equalTo(0));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void
    handles_arrays_of_empty_arrays() {
        Key<PVector<PVector<String>>> key = Key.named("emptiness");
        JsonRecordDeserialiser deserialiser = i -> i.add(key, fromList(fromList(fromString)));

        assertThat(key.extract(deserialiser.fromString("{\"emptiness\": [[],[],[]]}")), hasItems(TreePVector.<String>empty(), TreePVector.empty(), TreePVector.empty()));
    }

    @Test
    public void
    handles_arrays_of_objects() {
        ListKey<Record> addresses = ListKey.named("addresses");
        JsonRecordDeserialiser ds = i -> i.add(addresses, fromList(Address.deserialiser));
        Record r = ds.fromString("{\"addresses\":[{\"addressLines\":[\"line 1\",\"line 2\"]},{\"addressLines\":[]}]}");

        assertThat(addresses.join(Path.toIndex(0)).join(Address.addressLines).join(Path.toIndex(0)).extract(r), equalTo("line 1"));
        assertThat(addresses.join(Path.toIndex(0)).join(Address.addressLines).join(Path.toIndex(1)).extract(r), equalTo("line 2"));
        assertThat(addresses.join(Path.toIndex(1)).join(Address.addressLines).extract(r).size(), equalTo(0));
    }

    @Test
    public void
    handles_arrays_of_arrays() {
        ListKey<PVector<Integer>> rows = ListKey.named("rows");
        JsonRecordDeserialiser ds = i -> i.add(rows, fromList(fromList(fromInteger)));

        Record r = ds.fromString("{\"rows\":[[1,2,3],[4,5,6],[7,8,9]]}");

        assertThat(rows.join(Path.toIndex(1)).join(Path.toIndex(1)).apply(r).get(), equalTo(5));
    }

    @Test
    public void
    can_deserialise_a_list_of_records() {
        String json = "[{\"prefix\": \"0208\", \"number\": \"123456\"}, {\"prefix\": \"07775\", \"number\": \"654321\"}]";

        java.util.List<Record> numbers = fromList(readNumber).fromString(json);
        assertThat(numbers.get(0).get(prefix).get(), equalTo("0208"));
    }

    @Test
    public void
    can_deserialise_a_map_of_records() {
        String json = "{\"home\": {\"prefix\": \"0208\", \"number\": \"123456\"}, \"work\": {\"prefix\": \"07775\", \"number\": \"654321\"}}";

        Map<String, Record> numbers = fromMap(readNumber).fromString(json);
        assertThat(Path.<String, Record>toKey("home").join(prefix).extract(numbers), equalTo("0208"));
    }

    @Test
    public void
    can_deserialise_a_map_of_maps_of_records() {
        String json = "{\"no-one\": {}, " +
                "\"dominic\": {\"home\": {\"prefix\": \"0208\", \"number\": \"123456\"}, " +
                "\"work\": {\"prefix\": \"07775\", \"number\": \"654321\"}}}";

        Map<String, PMap<String, Record>> numbers = fromMap(fromMap(readNumber)).fromString(json);
        assertThat(Path.<String, PMap<String, Record>>toKey("dominic")
                .join(Path.<String, Record>toKey("home"))
                .join(prefix).extract(numbers), equalTo("0208"));
    }

    @Test
    public void
    can_deserialise_a_record_containing_a_map_of_records() {
        String json = "{\"numbers\": {\"home\": {\"prefix\": \"0208\", \"number\": \"123456\"}, \"work\": {\"prefix\": \"07775\", \"number\": \"654321\"}}}";

        Record theNumbers = readNumbers.fromString(json);
        assertThat(numbers.extract(theNumbers).get("home").get(prefix).get(), equalTo("0208"));
    }
}
