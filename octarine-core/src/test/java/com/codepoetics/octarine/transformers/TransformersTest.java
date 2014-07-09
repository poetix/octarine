package com.codepoetics.octarine.transformers;

import com.codepoetics.octarine.paths.Path;
import com.codepoetics.octarine.records.Key;
import com.codepoetics.octarine.records.Record;
import org.databene.contiperf.PerfTest;
import org.databene.contiperf.junit.ContiPerfRule;
import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class TransformersTest {

    private static final Key<String> sourceForename = Key.named("forename");
    private static final Key<String> sourceSurname = Key.named("surname");
    private static final Key<Integer> sourceAge = Key.named("age");
    private static final Key<String> sourceFavouriteColour = Key.named("favouriteColour");

    private static final Key<String> targetName = Key.named("name");
    private static final Key<Integer> targetAge = Key.named("age");
    private static final Key<String> targetFavouriteColour = Key.named("favouriteColour");

    Transformer<Record, Record> genericTransformer = c -> c
            .map(r -> sourceForename.get(r).orElse("") + " " + sourceSurname.get(r).orElse(""), targetName)
            .map(sourceAge, targetAge)
            .map(sourceFavouriteColour.mappedWith(String::toLowerCase), targetFavouriteColour);

    RecordTransformer<Record> recordTransformer = c -> c
            .map(r -> sourceForename.get(r).orElse("") + " " + sourceSurname.get(r).orElse(""), targetName)
            .map(sourceAge, targetAge)
            .map(sourceFavouriteColour.mappedWith(String::toLowerCase), targetFavouriteColour);

    @Rule
    public ContiPerfRule contiPerfRule = new ContiPerfRule();

    @Test
    @PerfTest(invocations = 1000)
    public void transforms_records() {
        transformMany(genericTransformer.seededWith(Record.empty()));
    }

    @Test
    @PerfTest(invocations = 1000)
    public void transforms_records_faster_with_record_transformer() {
        transformMany(recordTransformer);
    }

    private void transformMany(Function<Record, Record> t) {
        Record source = Record.of(
                sourceForename.of("Arthur"),
                sourceSurname.of("Putey"),
                sourceAge.of(23),
                sourceFavouriteColour.of("Puce")
        );

        List<Record> results = new ArrayList<>();
        for (int i=0; i<10000; i++) {
            results.add(t.apply(source));
        }

        assertThat(Path.<Record>toIndex(0).<String>join(targetName).extract(results), equalTo("Arthur Putey"));
        assertThat(Path.<Record>toIndex(0).<String>join(targetFavouriteColour).extract(results), equalTo("puce"));
    }

}
