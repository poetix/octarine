package com.codepoetics.octarine.records;

import com.codepoetics.octarine.functional.functions.F2;
import com.codepoetics.octarine.functional.lenses.Lens;
import org.junit.Test;

import java.util.Optional;
import java.util.function.Function;

import static com.codepoetics.octarine.Octarine.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class RecordTest {

    private static final Key<String> displayName = $("displayName");
    private static final Key<String> name = $("name", displayName.of("person_address"));
    private static final Key<Integer> age = $("age");
    private static final RecordKey address = $R("address");
    private static final ListKey<String> addressLines = $L("addressLines");
    private static final Key<String> notAppearingInThisRecord = $("not appearing in this record");
    private static final Record testRecord = $$(
            name.of("Arthur Putey"),
            age.of(43),
            address.of(
                    addressLines.of("23 Acacia Avenue", "Sunderland", "VB6 5UX")
            )
    );

    @Test
    public void
    keys_are_lenses() {
        Lens<Record, String> secondLineOfAddress =
                address.assertPresent().join(addressLines.assertPresent()).join(Lens.intoPVector(1));

        assertThat(secondLineOfAddress.get(testRecord), equalTo("Sunderland"));
        assertThat(secondLineOfAddress.set(testRecord, "Cirencester"),
                equalTo($$(
                        name.of("Arthur Putey"),
                        age.of(43),
                        address.of($$(
                                addressLines.of("23 Acacia Avenue", "Cirencester", "VB6 5UX")
                        ))
                ))
        );
    }

    @Test
    public void
    keys_are_extractors() {
        assertThat(address.test(testRecord), equalTo(true));
        assertThat(notAppearingInThisRecord.test(testRecord), equalTo(false));

        assertThat(name.extract(testRecord), equalTo("Arthur Putey"));
        assertThat(name.get(testRecord), equalTo(Optional.of("Arthur Putey")));
        assertThat(notAppearingInThisRecord.get(testRecord), equalTo(Optional.empty()));
    }

    @Test public void
    reading_from_a_value() {
        Key<Character> firstChar = $("firstChar");
        Key<Character> secondChar = $("secondChar");
        Key<Character> thirdChar = $("thirdChar");

        Function<String, Record> reader = Record.reader(
                firstChar.reading(F2.of(String::charAt, 0)),
                secondChar.reading(F2.of(String::charAt, 1)),
                thirdChar.reading(F2.of(String::charAt, 2)));

        assertThat(reader.apply("abc"), equalTo($$(firstChar.of('a'), secondChar.of('b'), thirdChar.of('c'))));
    }

}
