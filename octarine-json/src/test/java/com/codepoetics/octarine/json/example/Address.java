package com.codepoetics.octarine.json.example;

import com.codepoetics.octarine.api.ListKey;
import com.codepoetics.octarine.json.deserialisation.RecordDeserialiser;
import com.codepoetics.octarine.json.serialisation.RecordSerialiser;
import com.codepoetics.octarine.json.serialisation.Serialisers;
import com.codepoetics.octarine.keys.KeySet;
import com.codepoetics.octarine.validation.api.Schema;

import static com.codepoetics.octarine.Octarine.$L;
import static com.codepoetics.octarine.json.deserialisation.Deserialisers.ofString;

public interface Address {

    public static final KeySet mandatoryKeys = new KeySet();
    public static final ListKey<String> addressLines = mandatoryKeys.add($L("addressLines"));

    public static final RecordSerialiser serialiser = RecordSerialiser.builder()
            .writeList(addressLines, Serialisers.toString)
            .get();

    public static final RecordDeserialiser deserialiser = RecordDeserialiser.builder()
            .readList(addressLines, ofString)
            .get();

    public static final Schema<Address> schema = (record, validationErrors) ->
            mandatoryKeys.accept(record, validationErrors);
}
