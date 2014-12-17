package com.codepoetics.octarine.json;

import com.codepoetics.octarine.deserialisation.ParserMapperFactory;
import com.codepoetics.octarine.deserialisation.RecordValidatingDeserialiser;
import com.codepoetics.octarine.records.Record;
import com.fasterxml.jackson.core.JsonParser;

public interface JsonRecordDeserialiser extends
        JsonDeserialiser<Record>,       // wires in fromReader and fromString methods
        RecordValidatingDeserialiser<JsonParser>, // wires in valid method
        ParserMapperFactory<JsonParser> // wires in createMapper method, creates hook for configure method
    {

    @Override
    default public Record apply(JsonParser parser) {
        return JsonDeserialisers.readingKeys(createMapper()).apply(parser);
    }
}
