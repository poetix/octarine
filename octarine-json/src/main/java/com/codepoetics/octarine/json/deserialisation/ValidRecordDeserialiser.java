package com.codepoetics.octarine.json.deserialisation;

import com.codepoetics.octarine.api.Record;
import com.codepoetics.octarine.validation.api.RecordValidationException;
import com.codepoetics.octarine.validation.api.Schema;
import com.codepoetics.octarine.validation.api.Validation;
import com.fasterxml.jackson.core.JsonParser;

import java.io.Reader;

class ValidRecordDeserialiser<S> implements Deserialiser<Validation<S>> {

    private final Schema<S> schema;
    private final RecordDeserialiser reader;

    ValidRecordDeserialiser(Schema<S> schema, RecordDeserialiser reader) {
        this.schema = schema;
        this.reader = reader;
    }

    @Override
    public Validation<S> apply(JsonParser parser) {
        try {
            return validated(reader.apply(parser));
        } catch (RecordValidationException e) {
            return e.toValidation();
        }
    }

    @Override
    public Validation<S> fromReader(Reader reader) {
        try {
            return validated(this.reader.fromReader(reader));
        } catch (RecordValidationException e) {
            return e.toValidation();
        }
    }

    private Validation<S> validated(Record record) {
        return schema.validate(record);
    }
}
