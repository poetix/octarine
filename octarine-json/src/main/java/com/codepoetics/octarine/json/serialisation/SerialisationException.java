package com.codepoetics.octarine.json.serialisation;


import java.io.IOException;

public class SerialisationException extends RuntimeException {

    public SerialisationException(IOException cause) {
        super(cause);
    }

    public IOException getIOExceptionCause() {
        return (IOException) getCause();
    }
}
