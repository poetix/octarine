package com.codepoetics.octarine.json.example;

import com.codepoetics.octarine.api.ListKey;
import com.codepoetics.octarine.json.deserialisation.RecordDeserialiser;
import com.codepoetics.octarine.json.serialisation.JsonRecordSerialiser;
import com.codepoetics.octarine.json.serialisation.JsonSerialisers;
import com.codepoetics.octarine.keys.KeySet;
import com.codepoetics.octarine.validation.api.Schema;

import static com.codepoetics.octarine.json.deserialisation.Deserialisers.ofString;
import static com.codepoetics.octarine.json.serialisation.JsonSerialisers.asString;

public interface Address {

    public static final KeySet mandatoryKeys = new KeySet();
    public static final ListKey<String> addressLines = mandatoryKeys.addList("addressLines");

    public static final JsonRecordSerialiser serialiser = p ->
            p.add(addressLines, JsonSerialisers.asArray(asString));

    public static final RecordDeserialiser deserialiser = RecordDeserialiser.builder()
            .readList(addressLines, ofString)
            .get();

    public static final Schema<Address> schema = (record, validationErrors) ->
            mandatoryKeys.accept(record, validationErrors);
}
