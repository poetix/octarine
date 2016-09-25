/*
 * Copyright © 2017 VMware, Inc. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.codepoetics.octarine.bson;


import com.codepoetics.octarine.bson.data.Address;
import com.codepoetics.octarine.bson.data.Person;
import com.codepoetics.octarine.bson.deserialisation.BsonDeserialisers;
import com.codepoetics.octarine.bson.deserialisation.BsonRecordDeserialiser;
import com.codepoetics.octarine.records.Record;
import com.codepoetics.octarine.records.Valid;
import org.bson.BsonArray;
import org.bson.BsonDocument;
import org.bson.BsonInt32;
import org.bson.BsonString;
import org.junit.Test;
import org.pcollections.PVector;

import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.github.npathai.hamcrestopt.OptionalMatchers.isEmpty;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class BsonToRecord {

    public static final List<String> ADDRESS_LINES = Arrays.asList("13 Rue Morgue", "PO3 1TP");

    @Test public void
    convert_bson_to_record() {
        BsonDocument doc = new BsonDocument();
        doc.put("name", new BsonString("Dominic"));
        doc.put("age", new BsonInt32(39));

        BsonRecordDeserialiser deserialiser = BsonRecordDeserialiser.builder()
                .readString(Person.name)
                .readInteger(Person.age)
                .get();

        Record result = deserialiser.apply(doc);
        assertTrue("name not set", Person.name.get(result).isPresent());
        assertEquals("incorrect name", "Dominic", Person.name.get(result).get());
        assertTrue("age not set", Person.age.get(result).isPresent());
        assertEquals("incorrect age", 39, (int) Person.age.get(result).get());
    }

    @Test public void
    convert_bson_to_record_when_field_names_do_not_match_key_names() {
        BsonDocument doc = new BsonDocument();
        doc.put("foo", new BsonString("Dominic"));
        doc.put("bar", new BsonInt32(39));

        BsonRecordDeserialiser deserialiser = BsonRecordDeserialiser.builder()
                .readString(Person.name, "foo")
                .readInteger(Person.age, "bar")
                .get();

        Record result = deserialiser.apply(doc);
        assertTrue("name not set", Person.name.get(result).isPresent());
        assertEquals("incorrect name", "Dominic", Person.name.get(result).get());
        assertTrue("age not set", Person.age.get(result).isPresent());
        assertEquals("incorrect age", 39, (int) Person.age.get(result).get());
    }

    @Test public void
    convert_nested_bson_to_record() {
        BsonDocument doc = new BsonDocument();
        doc.put("name", new BsonString("Dominic"));
        doc.put("age", new BsonInt32(39));
        BsonDocument address = new BsonDocument();
        BsonArray addressLines = new BsonArray(ADDRESS_LINES.stream()
                .map(s -> new BsonString(s))
                .collect(Collectors.toList()));
        address.put("addressLines", addressLines);
        doc.put("address", address);

        BsonRecordDeserialiser addressDeserialiser = BsonRecordDeserialiser.builder()
                .readList(Address.addressLines, BsonDeserialisers.ofString)
                .get();

        BsonRecordDeserialiser deserialiser = BsonRecordDeserialiser.builder()
                .readString(Person.name)
                .readInteger(Person.age)
                .readValidRecord(Person.address, addressDeserialiser.validAgainst(Address.schema))
                .get();

        Record result = deserialiser.apply(doc);
        assertTrue("name not set", Person.name.get(result).isPresent());
        assertEquals("incorrect name", "Dominic", Person.name.get(result).get());
        assertTrue("age not set", Person.age.get(result).isPresent());
        assertEquals("incorrect age", 39, (int) Person.age.get(result).get());
        Optional<Valid<Address>> maybeAddress = Person.address.get(result);
        assertTrue("Address not present", maybeAddress.isPresent());
        Optional<PVector<String>> maybeLines = Address.addressLines.get(maybeAddress.get());
        assertThat(maybeLines, not(isEmpty()));
        assertEquals("Address lines don't match", ADDRESS_LINES, maybeLines.get());
    }

    @Test public void
    convert_bson_to_record_with_custom_type() {
        BsonDocument doc = new BsonDocument();
        doc.put("foo", new BsonString("Dominic"));
        doc.put("bar", new BsonInt32(39));
        doc.put("favouriteColour", new BsonString("0xFF0000"));

        BsonRecordDeserialiser deserialiser = BsonRecordDeserialiser.builder()
                .readString(Person.name)
                .readInteger(Person.age)
                .read(Person.favouriteColour, Person.colourFromBson)
                .get();

        Record result = deserialiser.apply(doc);
        assertThat(Person.favouriteColour.get(result), not(isEmpty()));
        assertEquals("mismatched colour", Color.RED, Person.favouriteColour.get(result).get());
    }
}
