package com.codepoetics.octarine.records;

import com.codepoetics.octarine.records.example.Address;
import com.codepoetics.octarine.records.example.Person;
import org.junit.Test;

import java.awt.*;
import java.util.Optional;

import static com.codepoetics.octarine.Octarine.$$;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.MatcherAssert.assertThat;

public class MutableRecordTest {

    @SuppressWarnings("unchecked")
    @Test
    public void
    mutable_records_capture_additions_and_removals() {
        MutableRecord mutable = $$(Person.name.of("Dominic"),
                Person.age.of(39),
                Person.favouriteColour.of(Color.RED),
                Person.address.of(Address.addressLines.of("13 Rue Morgue", "PO3 1TP"))).mutable();

        mutable.set(Person.age.of(40), Person.favouriteColour.of(Color.GRAY));
        mutable.unset(Person.address);

        assertThat(mutable.get(Person.age).get(), equalTo(40));
        assertThat(mutable.get(Person.favouriteColour).get(), equalTo(Color.GRAY));

        assertThat(mutable.get(Person.address), equalTo(Optional.empty()));

        assertThat(mutable.added(), equalTo($$(Person.age.of(40), Person.favouriteColour.of(Color.GRAY))));
        assertThat(mutable.removed(), hasItem(Person.address));

        assertThat(mutable, equalTo($$(
                Person.name.of("Dominic"),
                Person.age.of(40),
                Person.favouriteColour.of(Color.GRAY)
        )));

        assertThat(mutable.immutable(), equalTo($$(
                Person.name.of("Dominic"),
                Person.age.of(40),
                Person.favouriteColour.of(Color.GRAY)
        )));
    }
}
