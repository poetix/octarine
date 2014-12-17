package com.codepoetics.octarine.joins;

import com.codepoetics.octarine.Octarine;
import com.codepoetics.octarine.api.Key;
import com.codepoetics.octarine.api.ListKey;
import com.codepoetics.octarine.api.Record;
import org.hamcrest.CoreMatchers;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.codepoetics.octarine.Octarine.$;
import static com.codepoetics.octarine.Octarine.$$;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.MatcherAssert.assertThat;

public class RecordJoinerTest {

    public static final Key<String> authorName = $("authorName");
    public static final Key<String> id = $("id");
    public static final Key<String> authorId = $("authorId");
    public static final Key<String> bookName = $("bookName");
    public static final Key<String> publisherName = $("publisherName");
    public static final Key<String> publisherId = $("publisherId");
    public static final Record billsAndMoon = $$(
            publisherName.of("Bills And Moon"),
            id.of("BM")
    );
    public static final Record servo = $$(
            publisherName.of("Servo"),
            id.of("SV")
    );
    public static final List<Record> publishers = Arrays.asList(billsAndMoon, servo);
    public static final Record alanGoodyear = $$(
            authorName.of("Alan Goodyear"),
            id.of("AG")
    );
    public static final Record barbaraMoat = $$(
            authorName.of("Barbara Moat"),
            id.of("BM")
    );
    public static final Record leavisLacanian = $$(
            authorName.of("Leavis Lacanian"),
            id.of("LL")
    );
    public static final List<Record> authors = Arrays.asList(alanGoodyear, barbaraMoat, leavisLacanian);
    public static final Record cromulenceOfTruths = $$(
            authorId.of("AG"),
            publisherId.of("SV"),
            bookName.of("The Cromulence Of Truths")
    );
    public static final Record oligarchsSecret = $$(
            authorId.of("BM"),
            publisherId.of("BM"),
            bookName.of("Oligarch's Secret")
    );
    public static final Record butIsThisNot = $$(
            authorId.of("LL"),
            publisherId.of("SV"),
            bookName.of("But Is This Not: Dialectics for the Distracted")
    );
    public static final Record amorousEncounters = $$(
            authorId.of("AG"),
            publisherId.of("BM"),
            bookName.of("Amorous Encounters")
    );
    public static final List<Record> books = Arrays.asList(
            cromulenceOfTruths,
            oligarchsSecret,
            butIsThisNot,
            amorousEncounters
    );
    final ListKey<Record> authored = Octarine.$L("authored");

    @Test
    public void
    three_way_join() {
        List<Record> joined = RecordJoins.join(
                RecordJoins.join(books)
                        .on(authorId)
                        .to(id)
                        .manyToOne(authors))
                .on(publisherId)
                .to(id)
                .manyToOne(publishers)
                .map(r -> r.select(publisherName, authorName, bookName))
                .filter(authorName.is("Alan Goodyear"))
                .collect(Collectors.toList());

        assertThat(joined, CoreMatchers.hasItems(
                        $$(bookName.of("Amorous Encounters"),
                                authorName.of("Alan Goodyear"),
                                publisherName.of("Bills And Moon")),
                        $$(bookName.of("The Cromulence Of Truths"),
                                authorName.of("Alan Goodyear"),
                                publisherName.of("Servo"))
                )
        );
    }

    @Test
    public void
    one_to_many_join() {
        Record joined = RecordJoins.join(authors).on(id)
                .to(authorId).oneToMany(books, authored)
                .filter(authorName.is("Alan Goodyear"))
                .findFirst().get();

        assertThat(authored.extract(joined), hasItems(
                        cromulenceOfTruths, amorousEncounters)
        );
    }

}
