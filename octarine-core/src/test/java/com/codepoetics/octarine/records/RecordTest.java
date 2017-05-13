package com.codepoetics.octarine.records;

import com.codepoetics.octarine.functional.functions.F2;
import com.codepoetics.octarine.functional.lenses.Lens;
import com.codepoetics.octarine.records.example.Address;
import com.codepoetics.octarine.records.example.Person;
import org.junit.Test;
import org.pcollections.PVector;
import org.pcollections.TreePVector;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Function;

import static com.codepoetics.octarine.Octarine.$;
import static com.codepoetics.octarine.Octarine.$$;
import static com.codepoetics.octarine.Octarine.$L;
import static com.codepoetics.octarine.Octarine.$R;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItems;
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

    private static final Record validTestRecord = $$(
            Person.name.of("Tom Cat"),
            Person.age.of(9),
            Person.address.of(
                    Address.addressLines.of("1 Cat Road", "Smallville", "T11 M22")
            )
    );

    @Test
    public void
    getKeysFromRecord() {
        assertThat(testRecord.get(address).flatMap(address ->
                        address.get(addressLines)).get(),
                hasItems("23 Acacia Avenue", "Sunderland", "VB6 5UX"));
    }

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
    keys_are_lenses_on_valid_records() {
        Lens<Record, PVector<String>> linesLens =
                Person.address.assertPresent()
                        .join(Address.addressLines.assertValidPresent(Address.schema));

        assertThat(linesLens.get(validTestRecord), equalTo(Arrays.asList("1 Cat Road", "Smallville", "T11 M22")));
        assertThat(linesLens.set(validTestRecord, TreePVector.from(Arrays.asList("1 Mouse Street", "Mouseville", "M11 T22"))),
                equalTo($$(
                        Person.name.of("Tom Cat"),
                        Person.age.of(9),
                        Person.address.of(
                                Address.addressLines.of("1 Mouse Street", "Mouseville", "M11 T22")
                        )
                ))
        );

        Lens<Record, String> secondLineOfValidAddress = linesLens.join(Lens.intoPVector(1));

        assertThat(secondLineOfValidAddress.get(validTestRecord), equalTo("Smallville"));
        assertThat(secondLineOfValidAddress.set(validTestRecord, "Big Town"),
                equalTo($$(
                        Person.name.of("Tom Cat"),
                        Person.age.of(9),
                        Person.address.of(
                                Address.addressLines.of("1 Cat Road", "Big Town", "T11 M22")
                        )
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

    @Test
    public void
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
