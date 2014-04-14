package com.codepoetics.octarine.matchers;

import com.codepoetics.octarine.records.Key;
import com.codepoetics.octarine.records.Record;
import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;

public class ARecordTest {

    private static final Key<String> name = Key.named("name");
    private static final Key<Integer> age = Key.named("age");

    @Test public void
    matches_record_with_property_matchers() {
        Record person = Record.of(name.of("Arthur Putey"), age.of(42));

        assertThat(person, AnInstance.of(Record.class)
                .with(name, Present.andEqualTo("Arthur Putey"))
                .with(age, Present.andEqualTo(42)));
    }

    @Test public void
    lists_expected_properties_in_self_description() {
        Matcher<Record> matcher = AnInstance.of(Record.class)
                .with(name, Present.andEqualTo("Hubert Cumberdale"))
                .with(age, Present.andEqualTo(23));

        StringDescription description = new StringDescription();
        matcher.describeTo(description);

        assertThat(description.toString(), containsString("name: present and \"Hubert Cumberdale\""));
        assertThat(description.toString(), containsString("age: present and <23>"));
    }

    @Test public void
    describes_mismatch_meaningfully() {
        Record person = Record.of(name.of("Arthur Putey"));

        Matcher<Record> matcher = AnInstance.of(Record.class)
                .with(name, Present.andEqualTo("Hubert Cumberdale"))
                .with(age, Present.andEqualTo(23));

        StringDescription description = new StringDescription();
        matcher.describeMismatch(person, description);

        assertThat(description.toString(), containsString("age: was absent"));
        assertThat(description.toString(), containsString("name: was \"Arthur Putey\""));
    }
}
