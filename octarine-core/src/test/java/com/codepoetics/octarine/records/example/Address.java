package com.codepoetics.octarine.records.example;

import com.codepoetics.octarine.json.JsonDeserialiser;
import com.codepoetics.octarine.json.JsonSerialiser;
import com.codepoetics.octarine.records.KeySet;
import com.codepoetics.octarine.records.ListKey;
import com.codepoetics.octarine.records.Record;
import com.codepoetics.octarine.records.Schema;

import java.util.function.Consumer;

import static com.codepoetics.octarine.json.JsonDeserialiser.fromString;
import static com.codepoetics.octarine.json.JsonSerialiser.asString;

public interface Address {

    public static final KeySet mandatoryKeys = new KeySet();
    public static final ListKey<String> addressLines = mandatoryKeys.addList("addressLines");

    public static final Schema<Address> schema = new Schema<Address>() {
        @Override
        public void accept(Record record, Consumer<String> validationErrors) {
             mandatoryKeys.accept(record, validationErrors);
        }
    };

    public static final JsonSerialiser serialiser = p ->
            p.add(addressLines, p.asList(asString));

    public static final JsonDeserialiser deserialiser = i ->
            i.addList(addressLines, fromString);
}
