package com.codepoetics.octarine.records;

import com.codepoetics.octarine.records.example.Address;
import com.codepoetics.octarine.records.example.Person;
import org.junit.Test;

import java.awt.*;
import java.io.IOException;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class SerialisationTest {

    @Test public void
    writes_person_as_json() throws IOException {
        String json = Person.serialiser.toString(Record.of(
                Person.name.of("Dominic"),
                Person.age.of(39),
                Person.favouriteColour.of(Color.RED),
                Person.address.of(Address.addressLines.of("13 Rue Morgue", "PO3 1TP"))));

        assertThat(json, equalTo("{\"name\":\"Dominic\",\"age\":39,\"favourite colour\":\"0xFF0000\",\"address\":{\"addressLines\":[\"13 Rue Morgue\",\"PO3 1TP\"]}}"));
    }
}
