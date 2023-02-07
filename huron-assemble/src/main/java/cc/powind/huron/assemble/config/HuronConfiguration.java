package cc.powind.huron.assemble.config;

import cc.powind.huron.assemble.properties.HuronProperties;
import cc.powind.huron.core.model.Realtime;
import cc.powind.huron.core.model.RealtimeRegister;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(HuronProperties.class)
public class HuronConfiguration {

    private final Map<String, Class<? extends Realtime>> register = new ConcurrentHashMap<>();

    @Bean
    @ConditionalOnMissingBean
    public RealtimeRegister realtimeRegister() {
        return new RealtimeRegister() {

            @Override
            public void register(String alias, Class<? extends Realtime> clazz) {
                register.put(alias, clazz);
            }

            @Override
            public String getAlias(Class<? extends Realtime> clazz) {
                for (String key : register.keySet()) {
                    if (clazz != null && clazz.equals(register.get(key))) {
                        return key;
                    }
                }
                return null;
            }

            @Override
            public Class<? extends Realtime> getClazz(String alias) {
                return register.get(alias);
            }

            @Override
            public String[] getAlias() {
                return register.keySet().toArray(new String[0]);
            }
        };
    }
}
