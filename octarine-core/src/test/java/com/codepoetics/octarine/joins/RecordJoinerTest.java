package com.codepoetics.octarine.joins;

import com.codepoetics.octarine.records.Key;
import com.codepoetics.octarine.records.ListKey;
import com.codepoetics.octarine.records.Record;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.MatcherAssert.assertThat;

public class RecordJoinerTest {

    public static final Key<String> authorName = Key.named("authorName");
    public static final Key<String> id = Key.named("id");
    public static final Key<String> authorId = Key.named("authorId");
    public static final Key<String> bookName = Key.named("bookName");
    public static final Key<String> publisherName = Key.named("publisherName");
    public static final Key<String> publisherId = Key.named("publisherId");
    public static final Record billsAndMoon = Record.of(
            publisherName.of("Bills And Moon"),
            id.of("BM")
    );
    public static final Record servo = Record.of(
            publisherName.of("Servo"),
            id.of("SV")
    );
    public static final List<Record> publishers = Arrays.asList(billsAndMoon, servo);
    public static final Record alanGoodyear = Record.of(
            authorName.of("Alan Goodyear"),
            id.of("AG")
    );
    public static final Record barbaraMoat = Record.of(
            authorName.of("Barbara Moat"),
            id.of("BM")
    );
    public static final Record leavisLacanian = Record.of(
            authorName.of("Leavis Lacanian"),
            id.of("LL")
    );
    public static final List<Record> authors = Arrays.asList(alanGoodyear, barbaraMoat, leavisLacanian);
    public static final Record cromulenceOfTruths = Record.of(
            authorId.of("AG"),
            publisherId.of("SV"),
            bookName.of("The Cromulence Of Truths")
    );
    public static final Record oligarchsSecret = Record.of(
            authorId.of("BM"),
            publisherId.of("BM"),
            bookName.of("Oligarch's Secret")
    );
    public static final Record butIsThisNot = Record.of(
            authorId.of("LL"),
            publisherId.of("SV"),
            bookName.of("But Is This Not: Dialectics for the Distracted")
    );
    public static final Record amorousEncounters = Record.of(
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
    final ListKey<Record> authored = ListKey.named("authored");

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

        assertThat(joined, hasItems(
                        Record.of(bookName.of("Amorous Encounters"),
                                authorName.of("Alan Goodyear"),
                                publisherName.of("Bills And Moon")),
                        Record.of(bookName.of("The Cromulence Of Truths"),
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
