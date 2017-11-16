/*
 * Copyright © 2017 VMware, Inc. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.codepoetics.octarine.bson;


import com.codepoetics.octarine.bson.serialisation.BsonMapSerialiser;
import com.codepoetics.octarine.bson.serialisation.BsonRecordSerialiser;
import com.codepoetics.octarine.bson.serialisation.BsonSerialisers;
import com.codepoetics.octarine.records.Key;
import com.codepoetics.octarine.records.Record;
import org.bson.BsonDocument;
import org.bson.BsonInvalidOperationException;
import org.bson.types.ObjectId;
import org.junit.Test;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class SerialisationTest {
    Random random = new Random();

    @Test public void
    write_map_to_bson() {
        Map<String, Integer> mapToSerialise = new HashMap<>();
        mapToSerialise.put("foo", 42);
        mapToSerialise.put("bar", 666);

        BsonDocument doc = (BsonDocument) BsonMapSerialiser
                .writingValuesWith(BsonSerialisers.toInteger)
                .apply(mapToSerialise);

        assertEquals("invalid foo", 42, doc.getInt32("foo").getValue());
        assertEquals("invalid bar", 666, doc.getInt32("bar").getValue());
    }

    @Test public void
    write_map_with_null_values_to_bson() {
        Map<String, Integer> mapToSerialise = new HashMap<>();
        mapToSerialise.put("foo", 42);
        mapToSerialise.put("bar", null);

        BsonDocument doc = (BsonDocument) BsonMapSerialiser
                .writingValuesWith(BsonSerialisers.toInteger)
                .apply(mapToSerialise);

        assertEquals("invalid foo", 42, doc.getInt32("foo").getValue());
        try {
            Integer value = doc.getInt32("bar").getValue();
            fail("bar should not have been written to the document");
        } catch (BsonInvalidOperationException e) {
            // expected
        }
    }

    @Test public void
    serialise_boolean() {
        Key<Boolean> booleanKey = Key.named("my-value");

        BsonRecordSerialiser serialiser = BsonRecordSerialiser.builder()
                .writeBoolean(booleanKey)
                .get();

        Record trueRecord = Record.of(booleanKey.of(true));
        BsonDocument trueDoc = (BsonDocument) serialiser.apply(trueRecord);
        assertTrue("wasn't true", trueDoc.getBoolean("my-value").getValue());

        Record falseRecord = Record.of(booleanKey.of(false));
        BsonDocument falseDoc = (BsonDocument) serialiser.apply(falseRecord);
        assertFalse("wasn't false", falseDoc.getBoolean("my-value").getValue());
    }

    @Test public void
    serialise_date() {
        Date value = new Date();
        Key<Date> dateKey = Key.named("my-value");
        Record record = Record.of(dateKey.of(value));

        BsonDocument doc = (BsonDocument) BsonRecordSerialiser.builder()
                .writeDate(dateKey)
                .get()
                .apply(record);
        assertEquals("wrong date value", value.getTime(), doc.getDateTime("my-value").getValue());
    }

    @Test public void
    serialise_double() {
        double value = random.nextDouble();
        Key<Double> doubleKey = Key.named("my-value");
        Record record = Record.of(doubleKey.of(value));

        BsonDocument doc = (BsonDocument) BsonRecordSerialiser.builder()
                .writeDouble(doubleKey)
                .get()
                .apply(record);
        assertEquals("wrong double value", value, doc.getDouble("my-value").getValue(), 0.0001);
    }

    @Test public void
    serialise_integer() {
        int value = random.nextInt();
        Key<Integer> integerKey = Key.named("my-value");
        Record record = Record.of(integerKey.of(value));

        BsonDocument doc = (BsonDocument) BsonRecordSerialiser.builder()
                .writeInteger(integerKey)
                .get()
                .apply(record);
        assertEquals("wrong int value", value, doc.getInt32("my-value").getValue());
    }

    @Test public void
    serialise_long() {
        long value = random.nextLong();
        Key<Long> longKey = Key.named("my-value");
        Record record = Record.of(longKey.of(value));

        BsonDocument doc = (BsonDocument) BsonRecordSerialiser.builder()
                .writeLong(longKey)
                .get()
                .apply(record);
        assertEquals("wrong long value", value, doc.getInt64("my-value").getValue());
    }

    @Test public void
    serialise_object_id() {
        ObjectId value = new ObjectId(new Date(), random.nextInt(0xffffff));
        Key<ObjectId> idKey = Key.named("my-value");
        Record record = Record.of(idKey.of(value));

        BsonDocument doc = (BsonDocument) BsonRecordSerialiser.builder()
                .writeObjectId(idKey)
                .get()
                .apply(record);
        assertEquals("wrong ObjectId value", value, doc.getObjectId("my-value").getValue());
    }

    @Test public void
    serialise_string() {
        String value = UUID.randomUUID().toString();
        Key<String> stringKey = Key.named("my-value");
        Record record = Record.of(stringKey.of(value));

        BsonDocument doc = (BsonDocument) BsonRecordSerialiser.builder()
                .writeString(stringKey)
                .get()
                .apply(record);
        assertEquals("wrong string value", value, doc.getString("my-value").getValue());
    }
}
