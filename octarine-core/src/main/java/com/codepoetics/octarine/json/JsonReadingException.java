package com.codepoetics.octarine.json;


import java.io.IOException;

public class JsonReadingException extends RuntimeException {

    public JsonReadingException(IOException cause) {
        super(cause);
    }

    public IOException getIOExceptionCause() { return (IOException) getCause(); }
}
