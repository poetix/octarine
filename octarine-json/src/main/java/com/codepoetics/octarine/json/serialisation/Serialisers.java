package com.codepoetics.octarine.json.serialisation;

import com.fasterxml.jackson.core.JsonGenerator;

public final class Serialisers {
    private Serialisers() {
    }

    public static final SafeSerialiser<String> toString = JsonGenerator::writeString;
    public static final SafeSerialiser<Integer> toInteger = JsonGenerator::writeNumber;
    public static final SafeSerialiser<Double> toDouble = JsonGenerator::writeNumber;
    public static final SafeSerialiser<Long> toLong = JsonGenerator::writeNumber;
    public static final SafeSerialiser<Boolean> toBoolean = JsonGenerator::writeBoolean;

}
