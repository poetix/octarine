package com.codepoetics.octarine.records;

import com.codepoetics.octarine.records.example.Address;
import com.codepoetics.octarine.records.example.Person;
import com.codepoetics.octarine.validation.Valid;
import com.codepoetics.octarine.validation.Validation;
import org.junit.Test;

import java.awt.*;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.MatcherAssert.assertThat;

public class ValidationTest {

    @Test
    public void
    schemas_validate_valid_records() {
        Validation<Person> validationResult = Person.schema.validate(
                Person.name.of("Alice Cowley"),
                Person.age.of(42),
                Person.favouriteColour.of(Color.CYAN),
                Person.address.of(Address.addressLines.of("12 Penguin Way", "HR9 5BH")));

        assertThat(validationResult.isValid(), equalTo(true));

        Valid<Person> person = validationResult.get();
        assertThat(Person.name.extract(person), equalTo("Alice Cowley"));
    }

    @Test
    public void
    schemas_collect_validation_errors() {
        Validation<Person> validationResult = Person.schema.validate(
                Person.name.of("Eric"),
                Person.age.of(-2),
                Person.address.of(Address.addressLines.of("12 Penguin Way", "HR9 5BH"))
        );

        assertThat(validationResult.isValid(), equalTo(false));
        assertThat(validationResult.validationErrors(), hasItems(
                "Missing key \"favourite colour\"",
                "Age must be 0 or greater"
        ));
    }

    @Test(expected = Valid.RecordValidationException.class)
    public void
    exception_is_thrown_if_valid_record_key_populated_with_invalid_values() {
        Value addressValue = Person.address.of(Person.name.of("Address lines expected"));
    }
}
