package cc.powind.huron.core.utils;

import cc.powind.huron.core.model.Realtime;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.kafka.common.serialization.Deserializer;

import java.nio.charset.StandardCharsets;
import java.util.Map;

public class RealtimeDeserializer implements Deserializer<Realtime> {

    protected final Log log = LogFactory.getLog(getClass());

    private ObjectMapper mapper;

    private RealtimeKafkaTopic realtimeKafkaTopic;

    public ObjectMapper getMapper() {
        return mapper;
    }

    public void setMapper(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public RealtimeKafkaTopic getRealtimeKafkaTopic() {
        return realtimeKafkaTopic;
    }

    public void setRealtimeKafkaTopic(RealtimeKafkaTopic realtimeKafkaTopic) {
        this.realtimeKafkaTopic = realtimeKafkaTopic;
    }

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        // to do nothing
    }

    @Override
    public Realtime deserialize(String topic, byte[] data) {

        Class<? extends Realtime> clazz = realtimeKafkaTopic.getClazzFromTopic(topic);
        if (clazz == null) {
            throw new IllegalArgumentException("illegal topic");
        }

        try {
            return mapper.readValue(new String(data, StandardCharsets.UTF_8), clazz);
        } catch (JsonProcessingException e) {

            log.error("realtime serialization process error: " + e.getMessage());

            return null;
        }
    }

    @Override
    public void close() {
        // nothing to do
    }
}
