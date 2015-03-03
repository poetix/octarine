package com.codepoetics.octarine.json.serialisation;

import com.codepoetics.octarine.records.Key;
import com.codepoetics.octarine.records.Record;
import com.codepoetics.octarine.records.Value;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Stream;

import static com.codepoetics.octarine.Octarine.$$;

public class ReflectiveRecordSerialiser extends JsonSerializer<Record> {

    private static final ObjectMapper DEFAULT_MAPPER = mapperWith();

    public static ObjectMapper mapperWith(JsonSerializer<?>...extraSerialisers) {
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule simpleModule = new SimpleModule("SimpleModule", Version.unknownVersion());
        simpleModule.addSerializer(new ReflectiveRecordSerialiser());
        simpleModule.addSerializer(new StreamSerialiser());
        Stream.of(extraSerialisers).forEach(simpleModule::addSerializer);

        mapper.registerModules(simpleModule);

        return mapper;
    }

    private static final class StreamSerialiser extends JsonSerializer<Stream> {

        private static final class WrappedIOException extends RuntimeException {

            private final IOException e;

            private WrappedIOException(IOException e) {
                this.e = e;
            }

            public IOException getWrappedException() {
                return e;
            }
        }

        @Override
        public Class<Stream> handledType() { return Stream.class; }

        @Override
        public void serialize(Stream stream, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            jsonGenerator.writeStartArray();
            try {
                stream.forEach(item -> {
                    try {
                        serializerProvider.defaultSerializeValue(item, jsonGenerator);
                    } catch (IOException e) {
                        throw new WrappedIOException(e);
                    }
                });
            } catch (WrappedIOException e) {
                throw e.getWrappedException();
            }
            jsonGenerator.writeEndArray();
        }
    }

    public static String toJson(Value...values) throws JsonProcessingException {
        return toJson($$(values));
    }

    public static String toJson(Record record) throws JsonProcessingException {
        return DEFAULT_MAPPER.writeValueAsString(record);
    }

    public static String toJson(Record...records) throws JsonProcessingException {
        return toJson(Stream.of(records));
    }

    public static String toJson(Record record, JsonSerializer<?>...extraSerialisers) throws JsonProcessingException {
        return mapperWith(extraSerialisers).writeValueAsString(record);
    }

    public static String toJson(Collection<Record> records) throws JsonProcessingException {
        return toJson(records.stream());
    }

    public static String toJson(Stream<Record> records) throws JsonProcessingException {
        return DEFAULT_MAPPER.writeValueAsString(records);
    }

    public static String toJson(Collection<Record> records, JsonSerializer<?>...extraSerialisers) throws JsonProcessingException {
        return mapperWith(extraSerialisers).writeValueAsString(records);
    }

    public static String toJson(Stream<Record> records, JsonSerializer<?>...extraSerialisers) throws JsonProcessingException {
        return mapperWith(extraSerialisers).writeValueAsString(records);
    }

    public static String toJson(Map<String, Record> records) throws JsonProcessingException {
        return DEFAULT_MAPPER.writeValueAsString(records);
    }

    public static String toJson(Map<String, Record> records, JsonSerializer<?>...extraSerialisers) throws JsonProcessingException {
        return mapperWith(extraSerialisers).writeValueAsString(records);
    }

    @Override
    public Class<Record> handledType() { return Record.class; }

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
