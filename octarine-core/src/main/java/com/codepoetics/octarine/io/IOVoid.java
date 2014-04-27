package com.codepoetics.octarine.io;

import java.io.IOException;

public interface IOVoid extends IO<Void> {
    default Void run() throws IOException { runVoid(); return null; }
    void runVoid() throws IOException;
}
