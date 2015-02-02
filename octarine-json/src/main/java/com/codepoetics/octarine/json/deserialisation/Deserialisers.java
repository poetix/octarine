package com.codepoetics.octarine.json.deserialisation;

import com.codepoetics.octarine.records.Valid;
import com.codepoetics.octarine.records.Validation;
import com.fasterxml.jackson.core.JsonParser;

import java.util.function.Function;

public final class Deserialisers {

    private Deserialisers() {
    }

    public static final SafeDeserialiser<String> ofString = JsonParser::getValueAsString;
    public static final SafeDeserialiser<Integer> ofInteger = JsonParser::getIntValue;
    public static final SafeDeserialiser<Boolean> ofBoolean = JsonParser::getBooleanValue;
    public static final SafeDeserialiser<Long> ofLong = JsonParser::getLongValue;
    public static final SafeDeserialiser<Double> ofDouble = JsonParser::getDoubleValue;

    public static <S> SafeDeserialiser<Valid<S>> ofValid(Function<JsonParser, ? extends Validation<S>> extractor) {
        return parser -> extractor.apply(parser).get();
    }

}
