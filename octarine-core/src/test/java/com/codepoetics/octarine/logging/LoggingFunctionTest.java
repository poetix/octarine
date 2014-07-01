package com.codepoetics.octarine.logging;

import org.junit.Test;

import java.util.function.Function;

public class LoggingFunctionTest {

    @Test
    public void
    double_logging() {
        Function<String, Integer> singleLogging = String::length;
        Function<String, Integer> doubleLogging = singleLogging::apply;

        doubleLogging.apply("foo");
    }
}
