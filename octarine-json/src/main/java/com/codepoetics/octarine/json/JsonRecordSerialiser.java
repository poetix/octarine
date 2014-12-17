package com.codepoetics.octarine.json;

import com.codepoetics.octarine.api.Record;
import com.codepoetics.octarine.serialisation.GeneratorMapperFactory;
import com.fasterxml.jackson.core.JsonGenerator;

public interface JsonRecordSerialiser extends JsonSerialiser<Record>,
        GeneratorMapperFactory<JsonGenerator> {

    @Override
    default public void accept(JsonGenerator generator, Record record) {
        JsonSerialisers.writingKeys(createMapper()).accept(generator, record);
    }
}
