package cc.powind.huron.core.model;

import cc.powind.huron.rectifier.TopicMappings;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

public class RealtimeTopicMappings implements TopicMappings<Realtime> {

    private final Map<String, Class<? extends Realtime>> mappings;

    private String topicPrefix;

    public RealtimeTopicMappings(RealtimeRegister realtimeRegister) {

        if (realtimeRegister == null) {
            throw new IllegalArgumentException("realtime register must not be null");
        }

        this.mappings = realtimeRegister.getMappings();
    }

    public RealtimeTopicMappings(RealtimeRegister realtimeRegister, String topicPrefix) {

        if (realtimeRegister == null) {
            throw new IllegalArgumentException("realtime register must not be null");
        }

        if (StringUtils.isBlank(topicPrefix)) {
            throw new IllegalArgumentException("topic prefix must not be null");
        }

        this.mappings = realtimeRegister.getMappings();
        this.topicPrefix = topicPrefix;
    }

    @Override
    public String[] topics() {
        return mappings.keySet().stream().map(this::getTopic).toArray(String[]::new);
    }

    @Override
    public String topic(Realtime realtime) {

        for (String alias : mappings.keySet()) {
            if (realtime.getClass().equals(mappings.get(alias))) {
                return "storage_" + alias;
            }
        }

        return null;
    }

    @Override
    public Class clazz(String topic) {
        return mappings.get(getAlias(topic));
    }

    protected String getTopic(String alias) {
        return topicPrefix + "_" + alias;
    }

    protected String getAlias(String topic) {
        return StringUtils.removeStart(topic, topicPrefix + "_");
    }
}
