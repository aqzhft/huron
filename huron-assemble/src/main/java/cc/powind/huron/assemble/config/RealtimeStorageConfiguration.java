package cc.powind.huron.assemble.config;

import cc.powind.huron.assemble.properties.HuronProperties;
import cc.powind.huron.core.model.Realtime;
import cc.powind.huron.core.model.RealtimeMapper;
import cc.powind.huron.core.storage.DefaultRealtimeStorage;
import cc.powind.huron.core.storage.RealtimeStorage;
import cc.powind.huron.rectifier.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.ConnectionFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.KafkaAdminClient;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Properties;

@Configuration
public class RealtimeStorageConfiguration {

    private final Log log = LogFactory.getLog(getClass());

    @Autowired
    private HuronProperties properties;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TopicMappings<Realtime> topicMappings;

    @Autowired
    private List<RealtimeMapper> mappers;

    @Bean
    @ConditionalOnMissingBean
    public RealtimeStorage realtimeStorage(@Autowired @Qualifier("storageRectifier") Rectifier<Realtime> rectifier) {
        DefaultRealtimeStorage storage = new DefaultRealtimeStorage();
        storage.setMappers(mappers);
        storage.setRectifier(rectifier);
        return storage;
    }

    @Bean(name = "storageRectifier", initMethod = "init")
    @ConditionalOnProperty(prefix = "huron.storage", name = "rectifier", havingValue = "memory")
    public Rectifier<Realtime> blockingQueueRectifier() {

        BlockingQueueRectifier<Realtime> rectifier = new BlockingQueueRectifier<>();
        rectifier.setName("storage");
        rectifier.setMaxFetchWait(properties.getStorage().getMaxFetchWait());
        rectifier.setMinFetchSize(properties.getStorage().getMinFetchSize());
        rectifier.setMaxFetchSize(properties.getStorage().getMaxFetchSize());

        log.info(" ===========> blocking queue storage rectifier <=========== ");

        return rectifier;
    }

    @Configuration
    @ConditionalOnProperty(prefix = "huron.storage", name = "rectifier", havingValue = "kafka")
    public class KafkaRectifierConfiguration {

        @Bean(name = "storageRectifier", initMethod = "init")
        public Rectifier<Realtime> kafkaRectifier() {

            KafkaRectifier<Realtime> rectifier = new KafkaRectifier<>();
            rectifier.setName("storage");
            rectifier.setMaxFetchWait(properties.getStorage().getMaxFetchWait());
            rectifier.setMinFetchSize(properties.getStorage().getMinFetchSize());
            rectifier.setMaxFetchSize(properties.getStorage().getMaxFetchSize());
            rectifier.setObjectMapper(objectMapper);
            rectifier.setTopicMappings(topicMappings);
            rectifier.setAdminClient(adminClient());
            rectifier.setProducer(kafkaProducer());
            rectifier.setConsumer(kafkaConsumer());

            log.info(" ===========> kafka storage rectifier <=========== ");

            return rectifier;
        }

        private AdminClient adminClient() {
            Properties props = new Properties();
            props.put("bootstrap.servers", properties.getStorage().getKafka().getServers());
            return KafkaAdminClient.create(props);
        }

        private Consumer<String, String> kafkaConsumer() {

            Properties props = new Properties();
            props.put("bootstrap.servers", properties.getStorage().getKafka().getServers());
            props.put("group.id", properties.getStorage().getKafka().getGroupId());
            props.put("auto.offset.reset", properties.getStorage().getKafka().getAutoOffsetReset());
            props.put("fetch.max.bytes", properties.getStorage().getKafka().getFetchMaxSize());
            props.put("fetch.min.bytes", properties.getStorage().getKafka().getFetchMinSize());
            props.put("max.poll.records", properties.getStorage().getMaxFetchSize());   // Kafka默认是500
            props.put("fetch.max.wait.ms", properties.getStorage().getMaxFetchWait().intValue());

            return new KafkaConsumer<>(props, new StringDeserializer(), new StringDeserializer());
        }

        private Producer<String, String> kafkaProducer() {
            Properties props = new Properties();
            props.put("bootstrap.servers", properties.getStorage().getKafka().getServers());
            props.put("acks", properties.getStorage().getKafka().getAcks());
            return new KafkaProducer<>(props, new StringSerializer(), new StringSerializer());
        }
    }

    @Configuration
    @ConditionalOnProperty(prefix = "huron.storage", name = "rectifier", havingValue = "rabbit")
    public class RabbitRectifierConfiguration {

        @Bean(name = "storageRectifier", initMethod = "init")
        public Rectifier<Realtime> rabbitRectifier() {

            RabbitRectifier<Realtime> rectifier = new RabbitRectifier<>();
            rectifier.setName("storage");
            rectifier.setConnectionFactory(connectionFactory());
            rectifier.setObjectMapper(objectMapper);
            rectifier.setTopicMappings(topicMappings);

            log.info(" ===========> rabbitmq storage rectifier <=========== ");

            return rectifier;
        }

        private ConnectionFactory connectionFactory() {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost(properties.getStorage().getRabbit().getHost());
            factory.setPort(properties.getStorage().getRabbit().getPort());
            factory.setUsername(properties.getStorage().getRabbit().getUserName());
            factory.setPassword(properties.getStorage().getRabbit().getPassword());
            factory.setVirtualHost(properties.getStorage().getRabbit().getVirtualHost());
            return factory;
        }
    }

    @Bean(name = "storageRectifier", initMethod = "init")
    @ConditionalOnMissingBean
    public Rectifier<Realtime> defaultRectifier() {

        log.info(" ===========> default storage rectifier <=========== ");

        return new DefaultRectifier<>("storage");
    }
}
