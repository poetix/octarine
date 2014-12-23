package com.codepoetics.octarine.records.example;

import com.codepoetics.octarine.api.ListKey;
import com.codepoetics.octarine.keys.KeySet;
import com.codepoetics.octarine.validation.api.Schema;

import static com.codepoetics.octarine.Octarine.$L;

public interface Address {

    public static final KeySet mandatoryKeys = new KeySet();
    public static final ListKey<String> addressLines = mandatoryKeys.add($L("addressLines"));

    public static final Schema<Address> schema = (record, validationErrors) ->
            mandatoryKeys.accept(record, validationErrors);
}
