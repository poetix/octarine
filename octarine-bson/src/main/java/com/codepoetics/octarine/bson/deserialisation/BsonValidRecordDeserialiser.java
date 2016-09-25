/*
 * Copyright © 2017 VMware, Inc. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.codepoetics.octarine.bson.deserialisation;

import com.codepoetics.octarine.records.Record;
import com.codepoetics.octarine.records.RecordValidationException;
import com.codepoetics.octarine.records.Schema;
import com.codepoetics.octarine.records.Validation;
import org.bson.BsonInvalidOperationException;
import org.bson.BsonValue;


public class BsonValidRecordDeserialiser<T> implements SafeBsonDeserialiser<Validation<T>> {

    private final Schema<T> schema;

    private final BsonRecordDeserialiser reader;

    public BsonValidRecordDeserialiser(Schema<T> schema, BsonRecordDeserialiser reader) {
        this.schema = schema;
        this.reader = reader;
    }

    @Override
    public Validation<T> applyUnsafe(BsonValue p) throws BsonInvalidOperationException {
        try {
            return validated(reader.apply(p));
        } catch (RecordValidationException e) {
            return e.toValidation();
        }
    }

    private Validation<T> validated(Record record) {
        return schema.validate(record);
    }
}
