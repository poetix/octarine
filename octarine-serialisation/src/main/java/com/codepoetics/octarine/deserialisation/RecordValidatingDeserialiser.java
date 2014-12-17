package com.codepoetics.octarine.deserialisation;

import com.codepoetics.octarine.records.Record;
import com.codepoetics.octarine.validation.Schema;
import com.codepoetics.octarine.validation.Valid;
import com.codepoetics.octarine.validation.Validation;

import java.io.Reader;

public interface RecordValidatingDeserialiser<T> extends Deserialiser<T, Record> {
    default <S> Deserialiser<T, Validation<S>> validAgainst(Schema<S> schema) {
        return new Deserialiser<T, Validation<S>>() {
            @Override
            public Validation<S> apply(T parser) {
                try {
                    return validated(RecordValidatingDeserialiser.this.apply(parser));
                } catch (Valid.RecordValidationException e) {
                    return e.toValidation();
                }
            }

            @Override
            public Validation<S> fromReader(Reader reader) {
                try {
                    return validated(RecordValidatingDeserialiser.this.fromReader(reader));
                } catch (Valid.RecordValidationException e) {
                    return e.toValidation();
                }
            }

            private Validation<S> validated(Record record) {
                return schema.validate(record);
            }
        };
    }
}
