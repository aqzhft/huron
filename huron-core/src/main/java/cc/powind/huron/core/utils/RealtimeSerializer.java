package cc.powind.huron.core.utils;

import cc.powind.huron.core.exception.SerializationException;
import cc.powind.huron.core.model.Realtime;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Serializer;

import java.nio.charset.StandardCharsets;
import java.util.Map;

public class RealtimeSerializer implements Serializer<Realtime> {

    private ObjectMapper mapper;

    public ObjectMapper getMapper() {
        return mapper;
    }

    public void setMapper(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        // nothing to do
    }

    @Override
    public byte[] serialize(String topic, Realtime data) {

        if (data == null) {
            return null;
        }

        try {
            return mapper.writeValueAsString(data).getBytes(StandardCharsets.UTF_8);
        } catch (JsonProcessingException e) {
            throw new SerializationException("realtime serialization process error: " + e.getMessage());
        }
    }

    @Override
    public void close() {
        // to do nothing
    }
}
