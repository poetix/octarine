package com.codepoetics.octarine.logging;

import org.junit.Test;

import java.util.function.Function;

public class LoggingFunctionTest {

    @Test public void
    double_logging() {
        Function<String, Integer> singleLogging = (LoggedFunction<String, Integer>) String::length;
        Function<String, Integer> doubleLogging = (LoggedFunction<String, Integer>) singleLogging::apply;

        doubleLogging.apply("foo");
    }
}
