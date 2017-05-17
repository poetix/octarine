/*
 * Copyright © 2017 VMware, Inc. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.codepoetics.octarine.bson.serialisation;


import java.io.IOException;

public class BsonSerialisationException extends RuntimeException {

    public BsonSerialisationException(IOException cause) {
        super(cause);
    }

    public IOException getIOExceptionCause() {
        return (IOException) getCause();
    }
}
