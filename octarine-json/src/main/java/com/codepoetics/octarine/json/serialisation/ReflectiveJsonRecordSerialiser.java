package com.codepoetics.octarine.json.serialisation;

import com.codepoetics.octarine.api.Key;
import com.codepoetics.octarine.api.Record;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;

import java.io.IOException;
import java.util.Map;
import java.util.stream.Stream;

public class ReflectiveJsonRecordSerialiser extends JsonSerializer<Record> {

    public static ObjectMapper mapperWith(JsonSerializer<?>...extraSerialisers) {
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule simpleModule = new SimpleModule("SimpleModule",
                new Version(1,0,0,null));
        simpleModule.addSerializer(new ReflectiveJsonRecordSerialiser());
        Stream.of(extraSerialisers).forEach(simpleModule::addSerializer);

        mapper.registerModules(simpleModule);

        return mapper;
    }

    public static String toString(Record record) throws JsonProcessingException {
        return mapperWith().writeValueAsString(record);
    }

    @Override
    public Class handledType() { return Record.class; }

    @Override
    public void serialize(Record o, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        for (Map.Entry<Key<?>, Object> entry : o.values().entrySet()) {
            jsonGenerator.writeFieldName(entry.getKey().name());
            serializerProvider.defaultSerializeValue(entry.getValue(), jsonGenerator);
        }
        jsonGenerator.writeEndObject();
    }
}
