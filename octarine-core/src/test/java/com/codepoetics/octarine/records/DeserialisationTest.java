package com.codepoetics.octarine.records;

import com.codepoetics.octarine.json.JsonDeserialiser;
import com.codepoetics.octarine.paths.Path;
import com.codepoetics.octarine.records.example.Address;
import com.codepoetics.octarine.records.example.Person;
import org.junit.Test;
import org.pcollections.PVector;

import java.awt.*;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

public class DeserialisationTest {

    @Test public void
    deserialises_json_to_record() {
        Valid<Person> person = Person.deserialiser.readFromString(String.join("\n",
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
                "}"),
                Person.schema).get();

        assertThat(Person.name.extract(person), equalTo("Dominic"));
        assertThat(Person.age.extract(person), equalTo(39));
        assertThat(Person.favouriteColour.extract(person), equalTo(Color.RED));
        assertThat(Person.address.join(Address.addressLines).join(Path.toIndex(1))
                         .extract(person),
                equalTo("PO3 1TP")
        );
    }

    @Test public void
    returns_nested_validation_exceptions() {
        Validation<Person> personResult = Person.deserialiser.readFromString(String.join("\n",
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
                        "}"),
                Person.schema);

        assertThat(personResult.isValid(), equalTo(false));
        assertThat(personResult.validationErrors(), hasItem(containsString("addressLines")));
    }

    @Test public void
    handles_empty_arrays() {
        assertThat(Address.deserialiser.readFromString("{\"addressLines\":[]}").get(Address.addressLines).get().size(), equalTo(0));
    }

    @Test public void
    handles_arrays_of_objects() {
        ListKey<Record> addresses = ListKey.named("addresses");
        JsonDeserialiser ds = i -> i.addList(addresses, Address.deserialiser);
        Record r = ds.readFromString("{\"addresses\":[{\"addressLines\":[\"line 1\",\"line 2\"]},{\"addressLines\":[]}]}");

        assertThat(addresses.join(Path.toIndex(0)).join(Address.addressLines).join(Path.toIndex(0)).extract(r), equalTo("line 1"));
        assertThat(addresses.join(Path.toIndex(0)).join(Address.addressLines).join(Path.toIndex(1)).extract(r), equalTo("line 2"));
        assertThat(addresses.join(Path.toIndex(1)).join(Address.addressLines).extract(r).size(), equalTo(0));
    }

    @Test public void
    handles_arrays_of_arrays() {
        ListKey<PVector<Integer>> rows = ListKey.named("rows");
        JsonDeserialiser ds = i -> i.addList(rows, i.fromList(JsonDeserialiser.fromInteger));

        Record r = ds.readFromString("{\"rows\":[[1,2,3],[4,5,6],[7,8,9]]}");

        assertThat(rows.join(Path.toIndex(1)).join(Path.toIndex(1)).apply(r).get(), equalTo(5));
    }
}
