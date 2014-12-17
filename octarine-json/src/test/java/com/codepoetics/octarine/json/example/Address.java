package com.codepoetics.octarine.json.example;

import com.codepoetics.octarine.json.JsonRecordDeserialiser;
import com.codepoetics.octarine.json.JsonRecordSerialiser;
import com.codepoetics.octarine.json.JsonSerialisers;
import com.codepoetics.octarine.api.ListKey;
import com.codepoetics.octarine.api.Record;
import com.codepoetics.octarine.keys.KeySet;
import com.codepoetics.octarine.validation.api.Schema;

import java.util.function.Consumer;

import static com.codepoetics.octarine.json.JsonDeserialisers.fromList;
import static com.codepoetics.octarine.json.JsonDeserialisers.fromString;
import static com.codepoetics.octarine.json.JsonSerialisers.asString;

public interface Address {

    public static final KeySet mandatoryKeys = new KeySet();
    public static final ListKey<String> addressLines = mandatoryKeys.addList("addressLines");

    public static final JsonRecordSerialiser serialiser = p ->
            p.add(addressLines, JsonSerialisers.asArray(asString));

    public static final JsonRecordDeserialiser deserialiser = i ->
            i.add(addressLines, fromList(fromString));

    public static final Schema<Address> schema = new Schema<Address>() {
        @Override
        public void accept(Record record, Consumer<String> validationErrors) {
            mandatoryKeys.accept(record, validationErrors);
        }
    };
}
