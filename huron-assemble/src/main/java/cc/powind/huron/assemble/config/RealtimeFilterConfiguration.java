package cc.powind.huron.assemble.config;

import cc.powind.huron.assemble.properties.FilterProperties;
import cc.powind.huron.assemble.properties.HuronProperties;
import cc.powind.huron.core.filter.RedisRealtimeFilter;
import cc.powind.huron.core.model.RealtimeFilter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Configuration
public class RealtimeFilterConfiguration {

    private final Log log = LogFactory.getLog(getClass());

    @Autowired
    private HuronProperties properties;

    @Configuration
    @ConditionalOnProperty(prefix = "huron.filter", name = "type", havingValue = "redis")
    public class RedisRealtimeFilterConfiguration {

        @Bean
        @ConditionalOnMissingBean
        public RealtimeFilter realtimeFilter() {

            FilterProperties.RedisFilter redis = properties.getFilter().getRedis();

            RedisRealtimeFilter filter = new RedisRealtimeFilter();
            filter.setTtl(redis.getTtl());
            filter.setPool(jedisPool());

            log.info(" ===========> default storage rectifier <=========== ");

            return filter;
        }

        private JedisPoolConfig jedisPoolConfig() {
            JedisPoolConfig poolConfig = new JedisPoolConfig();
            poolConfig.setMaxTotal(10);
            poolConfig.setMaxIdle(10);
            poolConfig.setMinIdle(2);
            return poolConfig;
        }

        private JedisPool jedisPool() {
            FilterProperties.RedisFilter redis = properties.getFilter().getRedis();
            return new JedisPool(jedisPoolConfig(), redis.getHost(), redis.getPort(), redis.getTimeout());
        }
    }

}
