package com.codepoetics.octarine.records.example;

import com.codepoetics.octarine.json.JsonDeserialiser;
import com.codepoetics.octarine.json.JsonSerialiser;
import com.codepoetics.octarine.records.Key;
import com.codepoetics.octarine.records.KeySet;
import com.codepoetics.octarine.records.Schema;
import com.codepoetics.octarine.records.ValidRecordKey;

import java.awt.*;
import java.util.function.Function;

import static com.codepoetics.octarine.json.JsonDeserialiser.fromInteger;
import static com.codepoetics.octarine.json.JsonDeserialiser.fromString;
import static com.codepoetics.octarine.json.JsonSerialiser.asInteger;
import static com.codepoetics.octarine.json.JsonSerialiser.asString;

public interface Person extends Schema<Person> {

    public static final KeySet mandatoryKeys = new KeySet();
    public static final Key<String> name = mandatoryKeys.add("name");
    public static final Key<Integer> age = mandatoryKeys.add("age");
    public static final Key<Color> favouriteColour = mandatoryKeys.add("favourite colour");
    public static final ValidRecordKey<Address> address =
            mandatoryKeys.addValidRecord("address", Address.schema);

    public static final Person schema = (record, validationErrors) -> {
        mandatoryKeys.accept(record, validationErrors);
        age.from(record).ifPresent(a -> { if (a < 0) validationErrors.accept("Age must be 0 or greater"); });
    };

    public static final Function<Color, String> colourToString = c -> "0x" + Integer.toHexString(c.getRGB()).toUpperCase().substring(2);

    public static final JsonSerialiser serialiser = p ->
            p.add(Person.name, asString)
             .add(age, asInteger)
             .add(favouriteColour, (c, g) -> asString.accept(colourToString.apply(c), g))
             .add(address, Address.serialiser);

    public static final JsonDeserialiser deserialiser = i ->
            i.add(name, fromString)
             .add(age, fromInteger)
             .add(favouriteColour, fromString.andThen(Color::decode))
             .add(address, Address.deserialiser.validAgainst(Address.schema));
}
