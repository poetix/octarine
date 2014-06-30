package com.codepoetics.octarine.records.example;

import com.codepoetics.octarine.json.JsonRecordDeserialiser;
import com.codepoetics.octarine.json.JsonSerialiser;
import com.codepoetics.octarine.records.ListKey;
import com.codepoetics.octarine.records.Record;
import com.codepoetics.octarine.validation.KeySet;
import com.codepoetics.octarine.validation.Schema;

import java.util.function.Consumer;

import static com.codepoetics.octarine.json.JsonDeserialisers.fromList;
import static com.codepoetics.octarine.json.JsonDeserialisers.fromString;
import static com.codepoetics.octarine.json.JsonSerialiser.asString;

public interface Address {

    public static final KeySet mandatoryKeys = new KeySet();
    public static final ListKey<String> addressLines = mandatoryKeys.addList("addressLines");
    public static final JsonSerialiser serialiser = p ->
            p.add(addressLines, p.asList(asString));
    public static final JsonRecordDeserialiser deserialiser = i ->
            i.add(addressLines, fromList(fromString));
    public static final Schema<Address> schema = new Schema<Address>() {
        @Override
        public void accept(Record record, Consumer<String> validationErrors) {
            mandatoryKeys.accept(record, validationErrors);
        }
    };
}
