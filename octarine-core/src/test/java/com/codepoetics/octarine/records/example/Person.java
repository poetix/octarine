package com.codepoetics.octarine.records.example;

import com.codepoetics.octarine.records.Key;
import com.codepoetics.octarine.records.KeySet;
import com.codepoetics.octarine.records.Schema;
import com.codepoetics.octarine.records.ValidRecordKey;

import java.awt.*;
import java.util.function.Function;

import static com.codepoetics.octarine.Octarine.$;
import static com.codepoetics.octarine.Octarine.$V;

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

    public static final Function<Color, String> colourToString = c ->
            "0x" + Integer.toHexString(c.getRGB()).toUpperCase().substring(2);
}
