package com.codepoetics.octarine;

import com.codepoetics.octarine.records.*;
import com.codepoetics.octarine.testutils.ARecord;
import org.junit.Test;
import org.pcollections.HashTreePMap;

import java.util.HashMap;
import java.util.Map;

import static com.codepoetics.octarine.Octarine.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class OctarineTest {

    @Test public void
    dollar_methods() {
        Key<String> name = $("name");
        Key<Integer> age = $("age");
        MapKey<String> phoneNumbers = $M("phoneNumbers");
        RecordKey address = $R("address");
        ListKey<String> addressLines = $L("addressLines");
        Key<String> postcode = $("postcode");

        Record person = $$(
                name.of("Peter Warlock"),
                age.of(43));

        Record numbers = $$(phoneNumbers.of(
                "home", "01234 567890",
                "work", "0208 1234567",
                "mobile", "07771 234567"
        ));

        Record personWithNumbers = $$(person, numbers);
        Record completePerson = $$(personWithNumbers, address.of(
                addressLines.of(
                        "23 Acacia Avenue",
                        "Sunderland"
                ),
                postcode.of("VB6 5UX")
        ));

        Map<String, String> expectedNumbers = new HashMap<>();
        expectedNumbers.put("home", "01234 567890");
        expectedNumbers.put("work", "0208 1234567");
        expectedNumbers.put("mobile", "07771 234567");

        assertThat(completePerson, ARecord.instance()
                .with(name, "Peter Warlock")
                .with(age, 43)
                .with(phoneNumbers, HashTreePMap.from(expectedNumbers))
                .with(address, $$(
                                addressLines.of("23 Acacia Avenue", "Sunderland"),
                                postcode.of("VB6 5UX")
                        )
                ));
    }

    @Test public void
    long_methods() {
        Key<String> name = key("name");
        Key<Integer> age = key("age");
        MapKey<String> phoneNumbers = mapKey("phoneNumbers");
        RecordKey address = recordKey("address");
        ListKey<String> addressLines = listKey("addressLines");
        Key<String> postcode = key("postcode");

        Record person = record(
                name.of("Peter Warlock"),
                age.of(43));

        Record numbers = record(phoneNumbers.of(
                "home", "01234 567890",
                "work", "0208 1234567",
                "mobile", "07771 234567"
        ));

        Record personWithNumbers = record(person, numbers);
        Record completePerson = record(personWithNumbers, address.of(
                addressLines.of(
                        "23 Acacia Avenue",
                        "Sunderland"
                ),
                postcode.of("VB6 5UX")
        ));

        Map<String, String> expectedNumbers = new HashMap<>();
        expectedNumbers.put("home", "01234 567890");
        expectedNumbers.put("work", "0208 1234567");
        expectedNumbers.put("mobile", "07771 234567");

        assertThat(completePerson, ARecord.instance()
                .with(name, "Peter Warlock")
                .with(age, 43)
                .with(phoneNumbers, HashTreePMap.from(expectedNumbers))
                .with(address, $$(
                                addressLines.of("23 Acacia Avenue", "Sunderland"),
                                postcode.of("VB6 5UX")
                        )
                ));
    }

    @Test public void
    dollar_methods_with_metadata() {
        Key<String> displayName = $("displayName");

        Key<String> name = $("name", displayName.of("Name"));
        Key<Integer> age = $("age", $$(displayName.of("Age")));
        MapKey<String> phoneNumbers = $M("phoneNumbers", $$(displayName.of("Phone Numbers")));
        RecordKey address = $R("address", $$(displayName.of("Address")));
        ListKey<String> addressLines = $L("addressLines", $$(displayName.of("Address Lines")));
        Key<String> postcode = $("postcode", $$(displayName.of("Postcode")));

        assertThat(displayName.extract(name.metadata()), equalTo("Name"));
        assertThat(displayName.extract(age.metadata()), equalTo("Age"));
        assertThat(displayName.extract(phoneNumbers.metadata()), equalTo("Phone Numbers"));
        assertThat(displayName.extract(address.metadata()), equalTo("Address"));
        assertThat(displayName.extract(addressLines.metadata()), equalTo("Address Lines"));
        assertThat(displayName.extract(postcode.metadata()), equalTo("Postcode"));
    }

    @Test public void
    long_methods_with_metadata() {
        Key<String> displayName = key("displayName");

        Key<String> name = key("name", displayName.of("Name"));
        Key<Integer> age = key("age", record(displayName.of("Age")));
        MapKey<String> phoneNumbers = mapKey("phoneNumbers", record(displayName.of("Phone Numbers")));
        RecordKey address = recordKey("address", record(displayName.of("Address")));
        ListKey<String> addressLines = listKey("addressLines", record(displayName.of("Address Lines")));
        Key<String> postcode = key("postcode", record(displayName.of("Postcode")));

        assertThat(displayName.extract(name.metadata()), equalTo("Name"));
        assertThat(displayName.extract(age.metadata()), equalTo("Age"));
        assertThat(displayName.extract(phoneNumbers.metadata()), equalTo("Phone Numbers"));
        assertThat(displayName.extract(address.metadata()), equalTo("Address"));
        assertThat(displayName.extract(addressLines.metadata()), equalTo("Address Lines"));
        assertThat(displayName.extract(postcode.metadata()), equalTo("Postcode"));
    }
}
