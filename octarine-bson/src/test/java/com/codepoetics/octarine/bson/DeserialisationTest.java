/*
 * Copyright © 2017 VMware, Inc. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.codepoetics.octarine.bson;


import com.codepoetics.octarine.bson.deserialisation.BsonDeserialisers;
import com.codepoetics.octarine.bson.deserialisation.BsonMapDeserialiser;
import com.codepoetics.octarine.bson.deserialisation.BsonRecordDeserialiser;
import com.codepoetics.octarine.records.Key;
import com.codepoetics.octarine.records.Record;
import org.bson.BsonBoolean;
import org.bson.BsonDateTime;
import org.bson.BsonDocument;
import org.bson.BsonDouble;
import org.bson.BsonInt32;
import org.bson.BsonInt64;
import org.bson.BsonObjectId;
import org.bson.BsonString;
import org.bson.types.ObjectId;
import org.junit.Test;

import java.util.Date;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DeserialisationTest {
    Random random = new Random();

    @Test public void
    read_map_from_bson() {
        BsonDocument doc = new BsonDocument();
        doc.put("foo", new BsonInt32(42));
        doc.put("bar", new BsonInt32(666));

        Map<String, Integer> deserialised = BsonMapDeserialiser
                .readingValuesWith(BsonDeserialisers.ofInteger)
                .apply(doc);

        // need the cast to long to enable javac to disambiguate between varians of assertEquals
        assertEquals("invalid foo", 42, (int) deserialised.get("foo"));
        assertEquals("invalid bar", 666, (int) deserialised.get("bar"));
    }

    @Test public void
    deserialise_boolean() {
        Key<Boolean> booleanKey = Key.named("my-value");

        BsonRecordDeserialiser deserialiser = BsonRecordDeserialiser.builder()
                .readBoolean(booleanKey)
                .get();

        BsonDocument trueDoc = new BsonDocument();
        trueDoc.put("my-value", new BsonBoolean(true));
        Record trueRecord = deserialiser.apply(trueDoc);
        assertTrue("wasn't true", booleanKey.get(trueRecord).get());

        BsonDocument falseDoc = new BsonDocument();
        falseDoc.put("my-value", new BsonBoolean(false));
        Record falseRecord = Record.of(booleanKey.of(false));
        assertFalse("wasn't false", booleanKey.get(falseRecord).get());
    }

    @Test public void
    deserialise_date() {
        Date value = new Date();
        Key<Date> dateKey = Key.named("my-value");
        BsonDocument doc = new BsonDocument();
        doc.put("my-value", new BsonDateTime(value.getTime()));

        Record record = BsonRecordDeserialiser.builder()
                .readDate(dateKey)
                .get()
                .apply(doc);
        assertEquals("wrong date value", value, dateKey.get(record).get());
    }

    @Test public void
    deserialise_double() {
        double value = random.nextDouble();
        BsonDocument doc = new BsonDocument();
        doc.put("my-value", new BsonDouble(value));

        Key<Double> doubleKey = Key.named("my-value");
        Record record = BsonRecordDeserialiser.builder()
                .readDouble(doubleKey)
                .get()
                .apply(doc);
        assertEquals("wrong double value", value, doubleKey.get(record).get(), 0.0001);
    }

    @Test public void
    deserialise_integer() {
        int value = random.nextInt();
        BsonDocument doc = new BsonDocument();
        doc.put("my-value", new BsonInt32(value));
        Key<Integer> integerKey = Key.named("my-value");

        Record record = BsonRecordDeserialiser.builder()
                .readInteger(integerKey)
                .get()
                .apply(doc);
        assertEquals("wrong int value", value, (int) integerKey.get(record).get());
    }

    @Test public void
    deserialise_long() {
        long value = random.nextLong();
        BsonDocument doc = new BsonDocument();
        doc.put("my-value", new BsonInt64(value));
        Key<Long> longKey = Key.named("my-value");

        Record record = BsonRecordDeserialiser.builder()
                .readLong(longKey)
                .get()
                .apply(doc);
        assertEquals("wrong long value", value, (long) longKey.get(record).get());
    }

    @Test public void
    deserialise_object_id() {
        ObjectId value = new ObjectId(new Date(), random.nextInt(0xffffff));
        BsonDocument doc = new BsonDocument();
        doc.put("my-value", new BsonObjectId(value));
        Key<ObjectId> idKey = Key.named("my-value");

        Record record = BsonRecordDeserialiser.builder()
                .readObjectId(idKey)
                .get()
                .apply(doc);
        assertEquals("wrong ObjectId value", value, idKey.get(record).get());
    }

    @Test public void
    deserialise_string() {
        String value = UUID.randomUUID().toString();
        BsonDocument doc = new BsonDocument();
        doc.put("my-value", new BsonString(value));
        Key<String> stringKey = Key.named("my-value");

        Record record = BsonRecordDeserialiser.builder()
                .readString(stringKey)
                .get()
                .apply(doc);
        assertEquals("wrong string value", value, stringKey.get(record).get());
    }
}
