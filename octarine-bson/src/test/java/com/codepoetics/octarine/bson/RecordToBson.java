/*
 * Copyright © 2017 VMware, Inc. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.codepoetics.octarine.bson;


import com.codepoetics.octarine.bson.data.Address;
import com.codepoetics.octarine.bson.data.Person;
import com.codepoetics.octarine.bson.serialisation.BsonRecordSerialiser;
import com.codepoetics.octarine.bson.serialisation.BsonSerialisers;
import com.codepoetics.octarine.records.Record;
import org.bson.BsonArray;
import org.bson.BsonDocument;
import org.bson.BsonString;
import org.junit.Test;

import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class RecordToBson {

    public static final java.util.List<String> ADDRESS_LINES = Arrays.asList("13 Rue Morgue", "PO3 1TP");

    @Test public void
    convert_basic_record_to_bson() {
        Record person = Record.of(
                Person.name.of("Dominic"),
                Person.age.of(39));

        BsonRecordSerialiser serializer = BsonRecordSerialiser.builder()
                .writeString(Person.name)
                .writeInteger(Person.age)
                .get();

        BsonDocument doc = (BsonDocument) serializer.apply(person);
        assertEquals("Invalid name", "Dominic", doc.getString("name").getValue());
        assertEquals("invalid age", 39, doc.getInt32("age").getValue());
    }

    @Test public void
    convert_record_to_bson_when_field_names_do_not_match_key_names() {
        Record person = Record.of(
                Person.name.of("Dominic"),
                Person.age.of(39));

        BsonRecordSerialiser serializer = BsonRecordSerialiser.builder()
                .writeString(Person.name, "foo")
                .writeInteger(Person.age, "bar")
                .get();

        BsonDocument doc = (BsonDocument) serializer.apply(person);
        assertEquals("Invalid name", "Dominic", doc.getString("foo").getValue());
        assertEquals("invalid age", 39, doc.getInt32("bar").getValue());
    }

    @Test public void
    convert_record_with_sub_record_to_bson() {
        Record person = Record.of(
                Person.name.of("Dominic"),
                Person.age.of(39),
                Person.address.of(Address.addressLines.of(ADDRESS_LINES)));

        BsonRecordSerialiser addressSerialiser = BsonRecordSerialiser.builder()
                .writeList(Address.addressLines, BsonSerialisers.toString)
                .get();

        BsonRecordSerialiser serializer = BsonRecordSerialiser.builder()
                .writeString(Person.name)
                .writeInteger(Person.age)
                .write(Person.address, addressSerialiser)
                .get();

        BsonDocument doc = (BsonDocument) serializer.apply(person);
        assertEquals("Invalid name", "Dominic", doc.getString("name").getValue());
        assertEquals("invalid age", 39, doc.getInt32("age").getValue());
        BsonDocument bsonAddress = doc.getDocument("address");
        assertNotNull("address was null", bsonAddress);
        BsonArray bsonLines = bsonAddress.getArray("addressLines");
        assertNotNull("addressLines was null", bsonLines);
        List<String> bsonList = bsonLines.getValues().stream().map(v -> ((BsonString) v).getValue()).collect(Collectors.toList());
        assertEquals("Address contents don't match", ADDRESS_LINES, bsonList);
    }


    @Test public void
    convert_record_with_custom_type_to_bson() {
        Record person = Record.of(
                Person.name.of("Dominic"),
                Person.age.of(39),
                Person.favouriteColour.of(Color.RED));

        BsonRecordSerialiser serializer = BsonRecordSerialiser.builder()
                .writeString(Person.name)
                .writeInteger(Person.age)
                .write(Person.favouriteColour, Person.colourToBson)
                .get();

        BsonDocument doc = (BsonDocument) serializer.apply(person);
        assertEquals("Invalid name", "Dominic", doc.getString("name").getValue());
        assertEquals("invalid age", 39, doc.getInt32("age").getValue());
        assertEquals("Invalid colour", Person.colourToString.apply(Color.RED), doc.getString("favouriteColour").getValue());
    }
}
