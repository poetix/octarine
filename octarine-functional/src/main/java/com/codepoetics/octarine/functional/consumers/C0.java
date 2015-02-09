package com.codepoetics.octarine.functional.consumers;

public interface C0 extends Runnable {
    default void run() {
        accept();
    }

    void accept();

    default C0 andThen(C0 next) {
        return () -> {
            accept();
            next.accept();;
        };
    }
}
