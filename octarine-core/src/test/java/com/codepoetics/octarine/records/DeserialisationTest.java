package com.codepoetics.octarine.records;

import com.codepoetics.octarine.json.JsonDeserialiser;
import com.codepoetics.octarine.records.example.Address;
import com.codepoetics.octarine.records.example.Person;
import org.junit.Test;
import org.pcollections.PVector;

import java.awt.*;

import static com.codepoetics.octarine.json.JsonDeserialiser.fromInteger;
import static com.codepoetics.octarine.json.JsonDeserialiser.fromString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class DeserialisationTest {

    JsonDeserialiser addressDeserialiser = i ->
            i.add(Address.addressLines, i.fromList(fromString));

    JsonDeserialiser personDeserialiser = i ->
            i.add(Person.name, fromString)
             .add(Person.age, fromInteger)
             .add(Person.favouriteColour, fromString.andThen(Color::decode))
             .add(Person.address, addressDeserialiser.validAgainst(Address.schema));

    @Test public void
    deserialises_json_to_record() {
        Valid<Person> person = personDeserialiser.readFromString(
                "{\"name\":\"Dominic\",\"age\":39,\"favourite colour\":\"0xFF0000\",\"address\":{\"addressLines\":[\"13 Rue Morgue\",\"PO3 1TP\"]}}",
                Person.schema);

        assertThat(Person.name.from(person).orElse(""), equalTo("Dominic"));
        assertThat(Person.age.from(person).orElse(0), equalTo(39));
        assertThat(Person.favouriteColour.from(person).get(), equalTo(Color.RED));
        assertThat(Person.address.from(person).flatMap(Address.addressLines::from).get().get(1), equalTo("PO3 1TP"));
    }
}
