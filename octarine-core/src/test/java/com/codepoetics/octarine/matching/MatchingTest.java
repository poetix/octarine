package com.codepoetics.octarine.matching;

import com.codepoetics.octarine.records.Key;
import com.codepoetics.octarine.records.Record;
import com.codepoetics.octarine.records.example.Address;
import com.codepoetics.octarine.records.example.Person;
import org.junit.Test;

import java.awt.*;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;

public class MatchingTest {

    private static final Key<String> title = Key.named("title");
    private static final Key<String> director = Key.named("director");
    private static final Key<Integer> rating = Key.named("rating");

    private static final Matching<Record, String> matching = Matching.build(m ->
            m.matching(Person.schema, v -> "A valid person: " + v)
             .matching(Address.schema, a -> "A valid address: " + a)
             .when(rating.is(5))
                    .matching(title, director, (t, d) -> String.format("The 5-star movie '%s', directed by %s", t, d))
             .unless(rating.is(0))
                    .matching(title, director, (t, d) -> String.format("The movie '%s', directed by %s", t, d))
             .matching(title, director, (t, d) -> String.format("The utter stinker '%s', directed by %s", t, d)));

    private static final Record person = Record.of(
            Person.name.of("Richard Rotry"),
            Person.age.of(42),
            Person.favouriteColour.of(Color.CYAN),
            Person.address.of(
                    Address.addressLines.of("13 Newbury Drive", "Sutton", "SU1 4PG")
            )
    );

    @Test public void
    dispatches_on_schema() {
        assertThat(matching.apply(person).get(), containsString("A valid person"));
        assertThat(matching.apply(Person.address.from(person).get()).get(), containsString("A valid address"));
    }

    @Test public void
    dispatches_on_present_keys() {
        Record movie = Record.of(title.of("Brazil"), director.of("Terry Gilliam"));
        assertThat(matching.apply(movie).get(), containsString("The movie 'Brazil', directed by Terry Gilliam"));
    }

    @Test public void
    dispatches_on_predicates_and_present_keys() {
        Record movie1 = Record.of(title.of("Existenz"), director.of("David Cronenberg"), rating.of(4));
        Record movie2 = Record.of(title.of("Brazil"), director.of("Terry Gilliam"), rating.of(5));
        Record movie3 = Record.of(title.of("Howard the Duck"), director.of("Willard Huyck"), rating.of(0));

        assertThat(matching.apply(movie2).get(), containsString("The 5-star movie 'Brazil', directed by Terry Gilliam"));
        assertThat(matching.apply(movie1).get(), containsString("The movie 'Existenz', directed by David Cronenberg"));
        assertThat(matching.apply(movie3).get(), containsString("The utter stinker"));
    }


}
