package com.codepoetics.octarine.json;

import com.codepoetics.octarine.deserialisation.ParserMapperFactory;
import com.codepoetics.octarine.deserialisation.RecordDeserialiser;
import com.codepoetics.octarine.records.Record;
import com.fasterxml.jackson.core.JsonParser;

public interface JsonRecordDeserialiser extends JsonDeserialiser<Record>,
        RecordDeserialiser<JsonParser>,
        ParserMapperFactory<JsonParser> {

    @Override
    default public Record apply(JsonParser parser) {
        return JsonDeserialisers.readingKeys(createMapper()).apply(parser);
    }
}
