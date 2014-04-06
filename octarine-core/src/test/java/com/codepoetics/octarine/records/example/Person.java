package com.codepoetics.octarine.records.example;

import com.codepoetics.octarine.records.Key;
import com.codepoetics.octarine.records.KeySet;
import com.codepoetics.octarine.records.Schema;
import com.codepoetics.octarine.records.ValidRecordKey;

import java.awt.*;

public interface Person extends Schema<Person> {

    public static final KeySet mandatoryKeys = new KeySet();
    public static final Key<String> name = mandatoryKeys.add("name");
    public static final Key<Integer> age = mandatoryKeys.add("age");
    public static final Key<Color> favouriteColour = mandatoryKeys.add("favourite colour");
    public static final ValidRecordKey<Address> address =
            mandatoryKeys.addValidRecord("address", Address.schema);

    public static final Person schema = (record, validationErrors) -> {
        mandatoryKeys.accept(record, validationErrors);
        if (age.from(record).get() < 0) validationErrors.accept("Age must be 0 or greater");
    };
}
