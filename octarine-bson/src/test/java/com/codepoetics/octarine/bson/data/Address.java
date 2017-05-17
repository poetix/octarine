/*
 * Copyright © 2017 VMware, Inc. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.codepoetics.octarine.bson.data;

import com.codepoetics.octarine.records.KeySet;
import com.codepoetics.octarine.records.ListKey;
import com.codepoetics.octarine.records.Schema;

import static com.codepoetics.octarine.Octarine.$L;

public interface Address {

    KeySet mandatoryKeys = new KeySet();
    ListKey<String> addressLines = mandatoryKeys.add($L("addressLines"));

    Schema<Address> schema = (record, validationErrors) ->
            mandatoryKeys.accept(record, validationErrors);
}

