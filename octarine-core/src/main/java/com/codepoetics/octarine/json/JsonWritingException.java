package com.codepoetics.octarine.json;


import java.io.IOException;

public class JsonWritingException extends RuntimeException {

    public JsonWritingException(IOException cause) {
        super(cause);
    }

    void throwCause() throws IOException {
        throw (IOException) getCause();
    }
}
