/*
 * Copyright © 2017 VMware, Inc. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.codepoetics.octarine.bson.data;

import com.codepoetics.octarine.bson.deserialisation.BsonDeserialiser;
import com.codepoetics.octarine.bson.deserialisation.BsonDeserialisers;
import com.codepoetics.octarine.bson.serialisation.BsonSerialiser;
import com.codepoetics.octarine.bson.serialisation.BsonSerialisers;
import com.codepoetics.octarine.records.Key;
import com.codepoetics.octarine.records.KeySet;
import com.codepoetics.octarine.records.ValidRecordKey;

import java.awt.Color;
import java.util.function.Function;

import static com.codepoetics.octarine.Octarine.$;
import static com.codepoetics.octarine.Octarine.$V;

public interface Person {

    KeySet mandatoryKeys = new KeySet();
    Key<String> name = mandatoryKeys.add($("name"));
    Key<Integer> age = mandatoryKeys.add($("age"));
    Key<Color> favouriteColour = mandatoryKeys.add($("favouriteColour"));

    ValidRecordKey<Address> address =
            mandatoryKeys.add($V("address", Address.schema));

    Function<Color, String> colourToString = c -> "0x" + Integer.toHexString(c.getRGB()).toUpperCase().substring(2);

    BsonSerialiser<Color> colourToBson = c -> BsonSerialisers.toString.apply(colourToString.apply(c));

    BsonDeserialiser<Color> colourFromBson = b -> Color.decode(BsonDeserialisers.ofString.apply(b));
}
