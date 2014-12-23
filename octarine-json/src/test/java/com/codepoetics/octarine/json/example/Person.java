package com.codepoetics.octarine.json.example;

import com.codepoetics.octarine.api.Key;
import com.codepoetics.octarine.json.deserialisation.RecordDeserialiser;
import com.codepoetics.octarine.json.serialisation.RecordSerialiser;
import com.codepoetics.octarine.keys.KeySet;
import com.codepoetics.octarine.validation.api.Schema;
import com.codepoetics.octarine.validation.api.ValidRecordKey;

import java.awt.*;
import java.util.function.Function;

import static com.codepoetics.octarine.Octarine.*;

public interface Person {

    static final KeySet mandatoryKeys = new KeySet();
    static final Key<String> name = mandatoryKeys.add($("name"));
    static final Key<Integer> age = mandatoryKeys.add($("age"));
    public static final Key<Color> favouriteColour = mandatoryKeys.add($("favourite colour"));

    public static final ValidRecordKey<Address> address =
            mandatoryKeys.add($V("address", Address.schema));

    static final Schema<Person> schema = (record, validationErrors) -> {
        mandatoryKeys.accept(record, validationErrors);
        age.get(record).ifPresent(a -> {
            if (a < 0) validationErrors.accept("Age must be 0 or greater");
        });
    };

    public static final Function<Color, String> colourToString = c -> "0x" + Integer.toHexString(c.getRGB()).toUpperCase().substring(2);

    public static final RecordSerialiser serialiser = RecordSerialiser.builder()
            .writeString(name)
            .writeInteger(age)
            .writeToString(favouriteColour, colourToString)
            .write(address, Address.serialiser)
            .get();

    public static final RecordDeserialiser deserialiser = RecordDeserialiser.builder()
            .readString(name)
            .readInteger(age)
            .readFromString(favouriteColour, Color::decode)
            .readValidRecord(address, Address.deserialiser.validAgainst(Address.schema))
            .get();
}
