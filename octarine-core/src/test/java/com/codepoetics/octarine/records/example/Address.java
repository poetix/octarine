package com.codepoetics.octarine.records.example;

import com.codepoetics.octarine.records.KeySet;
import com.codepoetics.octarine.records.ListKey;
import com.codepoetics.octarine.records.Record;
import com.codepoetics.octarine.records.Schema;

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
