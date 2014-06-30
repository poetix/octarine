package com.codepoetics.octarine.consumers;

public interface C0 extends Runnable {
    default void run() {
        accept();
    }

    void accept();
}
