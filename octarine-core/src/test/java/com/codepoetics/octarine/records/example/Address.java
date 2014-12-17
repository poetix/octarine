package com.codepoetics.octarine.records.example;

import com.codepoetics.octarine.api.ListKey;
import com.codepoetics.octarine.api.Record;
import com.codepoetics.octarine.keys.KeySet;
import com.codepoetics.octarine.validation.api.Schema;

import java.util.function.Consumer;

public interface Address {

    public static final KeySet mandatoryKeys = new KeySet();
    public static final ListKey<String> addressLines = mandatoryKeys.addList("addressLines");

    public static final Schema<Address> schema = new Schema<Address>() {
        @Override
        public void accept(Record record, Consumer<String> validationErrors) {
            mandatoryKeys.accept(record, validationErrors);
        }
    };
}
