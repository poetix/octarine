package com.codepoetics.octarine.records;

import com.codepoetics.octarine.json.JsonSerialiser;
import com.codepoetics.octarine.records.example.Address;
import com.codepoetics.octarine.records.example.Person;
import com.fasterxml.jackson.core.JsonGenerator;
import org.junit.Test;
import org.pcollections.TreePVector;

import java.awt.*;
import java.io.IOException;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class SerialisationTest {

    public static JsonSerialiser addressSerialiser = new JsonSerialiser() {
        @Override
        public void projecting(Projections<JsonGenerator> projections) {
            projections.add(Address.addressLines, asList(asString));
        }
    };

    public static JsonSerialiser personSerialiser = new JsonSerialiser() {
        @Override
        public void projecting(Projections<JsonGenerator> projections) {
            projections.add(Person.name, asString)
                       .add(Person.age, asInteger)
                       .add(Person.favouriteColour, map(Color::toString, asString))
                       .add(Person.address, addressSerialiser);
        }
    };

    @Test public void
    writes_person_as_json() throws IOException {
        String json = personSerialiser.toString(Person.schema.validate(
                Person.name.of("Dominic"),
                Person.age.of(39),
                Person.favouriteColour.of(Color.RED),
                Person.address.of(Address.addressLines.of("13 Rue Morgue", "PO3 1TP"))).get());

        assertThat(json, equalTo("{\"name\":\"Dominic\",\"age\":39,\"favourite colour\":\"java.awt.Color[r=255,g=0,b=0]\",\"address\":{\"addressLines\":[\"13 Rue Morgue\",\"PO3 1TP\"]}}"));
    }
}
