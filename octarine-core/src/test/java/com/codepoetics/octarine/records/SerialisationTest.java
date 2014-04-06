package com.codepoetics.octarine.records;

import com.codepoetics.octarine.json.JsonSerialiser;
import com.codepoetics.octarine.records.example.Address;
import com.codepoetics.octarine.records.example.Person;
import org.junit.Test;

import java.awt.*;
import java.io.IOException;

import static com.codepoetics.octarine.json.JsonSerialiser.asInteger;
import static com.codepoetics.octarine.json.JsonSerialiser.asString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class SerialisationTest {

    public static JsonSerialiser addressSerialiser = p ->
            p.add(Address.addressLines, p.asList(asString));

    public static JsonSerialiser personSerialiser = p ->
            p.add(Person.name, asString)
             .add(Person.age, asInteger)
             .add(Person.favouriteColour, p.map(c -> "0x" + Integer.toHexString(c.getRGB()).toUpperCase().substring(2), asString))
             .add(Person.address, addressSerialiser);

    @Test public void
    writes_person_as_json() throws IOException {
        String json = personSerialiser.toString(Record.of(
                Person.name.of("Dominic"),
                Person.age.of(39),
                Person.favouriteColour.of(Color.RED),
                Person.address.of(Address.addressLines.of("13 Rue Morgue", "PO3 1TP"))));

        assertThat(json, equalTo("{\"name\":\"Dominic\",\"age\":39,\"favourite colour\":\"0xFF0000\",\"address\":{\"addressLines\":[\"13 Rue Morgue\",\"PO3 1TP\"]}}"));
    }
}
