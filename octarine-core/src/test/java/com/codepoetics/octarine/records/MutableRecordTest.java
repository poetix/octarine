package com.codepoetics.octarine.records;

import com.codepoetics.octarine.morphisms.FluentCollection;
import com.codepoetics.octarine.records.example.Address;
import com.codepoetics.octarine.records.example.Person;
import org.junit.Test;

import java.awt.*;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class MutableRecordTest {

    @Test
    public void
    mutable_records_capture_additions_and_removals() {
        MutableRecord mutable = Record.of(Person.name.of("Dominic"),
                Person.age.of(39),
                Person.favouriteColour.of(Color.RED),
                Person.address.of(Address.addressLines.of("13 Rue Morgue", "PO3 1TP"))).mutable();

        mutable.set(Person.age.of(40), Person.favouriteColour.of(Color.GRAY));
        mutable.unset(Person.address);

        assertThat(mutable.get(Person.age).get(), equalTo(40));
        assertThat(mutable.get(Person.favouriteColour).get(), equalTo(Color.GRAY));

        assertThat(mutable.get(Person.address), equalTo(Optional.empty()));

        assertThat(mutable.added(), equalTo(Record.of(Person.age.of(40), Person.favouriteColour.of(Color.GRAY))));
        assertThat(mutable.removed(), equalTo(FluentCollection.fromVarArgs(Person.address).toPSet()));

        assertThat(mutable.immutable(), equalTo(Record.of(
                Person.name.of("Dominic"),
                Person.age.of(40),
                Person.favouriteColour.of(Color.GRAY)
        )));
    }
}
