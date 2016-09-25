/*
 * Copyright © 2017 VMware, Inc. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.codepoetics.octarine.bson.serialisation;


import org.bson.BsonValue;

import java.util.function.Function;

public interface BsonSerialiser<R> extends Function<R, BsonValue> {
}
