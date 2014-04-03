package com.codepoetics.octarine.records;

import org.junit.Test;

import java.awt.*;
import java.util.function.Consumer;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.MatcherAssert.assertThat;

public class ValidationTest {

    public static interface Address {

        public static final KeySet mandatoryKeys = new KeySet();
        public static final ListKey<String> addressLines = mandatoryKeys.addList("addressLines");

        public static final Schema<Address> schema = new Schema<Address>() {
            @Override
            public void accept(Record record, Consumer<String> validationErrors) {
                 mandatoryKeys.accept(record, validationErrors);
            }
        };
    }
    public static interface Person extends Schema<Person> {

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

    @Test public void
    schemas_validate_valid_records() {
        Validation<Person> validationResult = Person.schema.validate(
                Person.name.of("Alice Cowley"),
                Person.age.of(42),
                Person.favouriteColour.of(Color.CYAN),
                Person.address.of(Address.addressLines.of("12 Penguin Way", "HR9 5BH")));

        assertThat(validationResult.isValid(), equalTo(true));

        Valid<Person> person = validationResult.get();
        assertThat(Person.name.from(person, ""), equalTo("Alice Cowley"));
    }

    @Test public void
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

    @Test(expected=IllegalStateException.class) public void
    exception_is_thrown_if_valid_record_key_populated_with_invalid_values() {
        Value addressValue = Person.address.of(Person.name.of("Address lines expected"));
    }
}
