package cc.powind.huron.assemble.config;

import cc.powind.huron.core.model.Realtime;
import cc.powind.huron.core.model.RealtimeRegister;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class BasicConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public ExecutorService executorService() {
        return Executors.newFixedThreadPool(10);
    }

    @Bean
    @ConditionalOnMissingBean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.registerModules(new Jdk8Module());
        objectMapper.registerModules(new JavaTimeModule());
        objectMapper.configure(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS, false);
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true);
        objectMapper.configure(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS, false);
        return objectMapper;
    }

    @Bean
    @ConditionalOnMissingBean
    public RealtimeRegister realtimeRegister() {

        return new RealtimeRegister() {

            private final Map<String, Class<? extends Realtime>> map = new HashMap<>();

            @Override
            public void register(String alias, Class<? extends Realtime> clazz) {
                map.put(alias, clazz);
            }

            @Override
            public String getAlias(Class<? extends Realtime> clazz) {
                for (String alias : map.keySet()) {
                    if (clazz.equals(map.get(alias))) {
                        return alias;
                    }
                }
                return null;
            }

            @Override
            public Class<? extends Realtime> getClazz(String alias) {
                return map.get(alias);
            }

            @Override
            public String[] getAlias() {
                return map.keySet().toArray(new String[0]);
            }

            @Override
            public Map<String, Class<? extends Realtime>> getMappings() {
                return map;
            }
        };
    }
}
