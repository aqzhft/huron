package cc.powind.huron.assemble.config;

import cc.powind.huron.assemble.properties.HuronProperties;
import cc.powind.huron.core.filter.RedisRealtimeFilter;
import cc.powind.huron.core.model.RealtimeFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;

@Configuration(proxyBeanMethods = false)
public class RealtimeFilterConfiguration {

    @Autowired
    private HuronProperties properties;

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "huron.filter", name = "redis-realtime-filter", havingValue = "true")
    public RealtimeFilter redisRealtimeFilter() {
        RedisRealtimeFilter filter = new RedisRealtimeFilter();
        filter.setTimeout(properties.getFilter().getTimeout().intValue());
        filter.setPool(filterJedisPool());
        return filter;
    }

    @Bean
    public JedisPool filterJedisPool() {
        return null;
    }
}
