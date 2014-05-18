package com.codepoetics.octarine.logging;

import java.util.function.Function;
import java.util.logging.Logger;

public interface LoggedFunction<S, T> extends Function<S, T> {
    static final Logger logger = Logger.getLogger(LoggedFunction.class.getName());
    @Override
    default public T apply(S input) {
        T result = applyLogged(input);
        logger.info(String.format("LoggedFormat %s called with %s as input, returning %s", this, input, result));
        return result;
    }

    T applyLogged(S input);

    static Function<String, Integer> getLengthLogged2 = (LoggedFunction<String, Integer>) String::length;
}
