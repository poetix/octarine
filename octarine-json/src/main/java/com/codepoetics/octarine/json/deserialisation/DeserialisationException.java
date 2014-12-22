package com.codepoetics.octarine.json.deserialisation;

import java.io.IOException;

public class DeserialisationException extends RuntimeException {
    public DeserialisationException(IOException e) {
        super(e);
    }
}
