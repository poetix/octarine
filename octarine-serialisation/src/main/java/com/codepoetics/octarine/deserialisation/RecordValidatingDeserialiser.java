package com.codepoetics.octarine.deserialisation;

import com.codepoetics.octarine.api.Record;
import com.codepoetics.octarine.validation.api.RecordValidationException;
import com.codepoetics.octarine.validation.api.Schema;
import com.codepoetics.octarine.validation.api.Validation;

import java.io.Reader;

public interface RecordValidatingDeserialiser<T> extends Deserialiser<T, Record> {
    default <S> Deserialiser<T, Validation<S>> validAgainst(Schema<S> schema) {
        return new Deserialiser<T, Validation<S>>() {
            @Override
            public Validation<S> apply(T parser) {
                try {
                    return validated(RecordValidatingDeserialiser.this.apply(parser));
                } catch (RecordValidationException e) {
                    return e.toValidation();
                }
            }

            @Override
            public Validation<S> fromReader(Reader reader) {
                try {
                    return validated(RecordValidatingDeserialiser.this.fromReader(reader));
                } catch (RecordValidationException e) {
                    return e.toValidation();
                }
            }

            private Validation<S> validated(Record record) {
                return schema.validate(record);
            }
        };
    }
}
