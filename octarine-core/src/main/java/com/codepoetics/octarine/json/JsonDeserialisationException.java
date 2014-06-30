package com.codepoetics.octarine.json;

import java.io.IOException;

public class JsonDeserialisationException extends RuntimeException {
    public JsonDeserialisationException(IOException e) {
        super(e);
    }
}
