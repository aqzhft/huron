package cc.powind.huron.assemble.config;

import cc.powind.huron.assemble.properties.HuronProperties;
import cc.powind.huron.core.model.RealtimeMapper;
import cc.powind.huron.core.model.Realtime;
import cc.powind.huron.core.model.RealtimeRegister;
import cc.powind.huron.core.storage.RealtimeStorage;
import cc.powind.huron.core.storage.*;
import cc.powind.huron.core.utils.RealtimeDeserializer;
import cc.powind.huron.core.utils.RealtimeKafkaTopic;
import cc.powind.huron.core.utils.RealtimeRabbitmqQueue;
import cc.powind.huron.core.utils.RealtimeSerializer;
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
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.MQConsumer;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.MQProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;

@Configuration(proxyBeanMethods = false)
public class RealtimeStorageConfiguration {

    private final Log log = LogFactory.getLog(getClass());

    @Autowired
    private HuronProperties properties;

    @Autowired
    private ObjectMapper mapper;

    @Lazy
    @Autowired
    private RealtimeRegister register;

    @Autowired
    private ExecutorService executorService;

    @Autowired
    private List<RealtimeMapper> realtimeMappers;

    @Bean(initMethod = "init")
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "huron.storage.default", name = "enable", havingValue = "true")
    public RealtimeStorage defaultStorage(ExecutorService executorService) {

        log.info("=====================> init default collector storage <=====================");

        DefaultStorage storage = new DefaultStorage();
        storage.setMappers(realtimeMappers);
        return storage;
    }

    @Bean(initMethod = "init")
    @ConditionalOnMissingBean
    @ConditionalOnBean(ExecutorService.class)
    @ConditionalOnProperty(prefix = "huron.storage.blocking", name = "enable", havingValue = "true")
    public RealtimeStorage blockingQueueStorage(ExecutorService executorService) {

        log.info("=====================> init blocking-queue collector storage <=====================");

        BlockingQueueStorage storage = new BlockingQueueStorage();
        storage.setExecutorService(executorService);
        storage.setMaxFetchWait(properties.getStorage().getMaxFetchWait());
        storage.setMinFetchSize(properties.getStorage().getMinFetchSize());
        storage.setMaxFetchSize(properties.getStorage().getMaxFetchSize());
        storage.setMappers(realtimeMappers);
        return storage;
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnProperty(prefix = "huron.storage.kafka", name = "enable", havingValue = "true")
    public class KafkaStorageConfiguration {

        @Bean(initMethod = "init")
        @ConditionalOnMissingBean
        @ConditionalOnBean(ExecutorService.class)
        public RealtimeStorage kafkaStorage() {

            log.info("=====================> init kafka-queue collector storage <=====================");

            KafkaStorage storage = new KafkaStorage();
            storage.setMaxFetchWait(properties.getStorage().getMaxFetchWait());
            storage.setAdminClient(adminClient());
            storage.setConsumer(kafkaStorageConsumer());
            storage.setProducer(storageProducer());
            storage.setKafkaTopic(new RealtimeKafkaTopic(register));
            storage.setExecutorService(executorService);
            storage.setMappers(realtimeMappers);
            return storage;
        }

        private AdminClient adminClient() {

            Properties props = new Properties();
            props.put("bootstrap.servers", properties.getStorage().getKafka().getServers());

            return KafkaAdminClient.create(props);
        }

        private Consumer<String, Realtime> kafkaStorageConsumer() {

            Properties props = new Properties();
            props.put("bootstrap.servers", properties.getStorage().getKafka().getServers());
            props.put("group.id", properties.getStorage().getKafka().getGroupId());
            props.put("auto.offset.reset", properties.getStorage().getKafka().getAutoOffsetReset());
            props.put("fetch.max.bytes", properties.getStorage().getKafka().getFetchMaxSize());
            props.put("fetch.min.bytes", properties.getStorage().getKafka().getFetchMinSize());
            props.put("max.poll.records", properties.getStorage().getMaxFetchSize());   // Kafka默认是500
            props.put("fetch.max.wait.ms", properties.getStorage().getMaxFetchWait().intValue());

            RealtimeDeserializer deserializer = new RealtimeDeserializer();
            deserializer.setMapper(mapper);
            deserializer.setRealtimeKafkaTopic(new RealtimeKafkaTopic(register));

            return new KafkaConsumer<>(props, new StringDeserializer(), deserializer);
        }

        private Producer<String, Realtime> storageProducer() {

            Properties props = new Properties();
            props.put("bootstrap.servers", properties.getStorage().getKafka().getServers());
            props.put("acks", properties.getStorage().getKafka().getAcks());

            RealtimeSerializer serializer = new RealtimeSerializer();
            serializer.setMapper(mapper);

            return new KafkaProducer<>(props, new StringSerializer(), serializer);
        }
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnProperty(prefix = "huron.storage.rabbitmq", name = "enable", havingValue = "true")
    public class RabbitmqStorageConfiguration {

        @Bean(initMethod = "init")
        @ConditionalOnMissingBean
        public RealtimeStorage rabbitmqStorage() {

            log.info("=====================> init rabbitmq-queue collector storage <=====================");

            RabbitmqStorage storage = new RabbitmqStorage();

            storage.setConnectionFactory(connectionFactory());
            storage.setMapper(mapper);
            storage.setRabbitmqQueue(new RealtimeRabbitmqQueue(register));
            storage.setExecutorService(executorService);
            storage.setMappers(realtimeMappers);

            return storage;
        }

        private ConnectionFactory connectionFactory() {

            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost(properties.getStorage().getRabbitmq().getHost());
            factory.setPort(properties.getStorage().getRabbitmq().getPort());
            factory.setUsername(properties.getStorage().getRabbitmq().getUserName());
            factory.setPassword(properties.getStorage().getRabbitmq().getPassword());
            factory.setVirtualHost(properties.getStorage().getRabbitmq().getVirtualHost());

            return factory;
        }

    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnMissingBean
    @ConditionalOnBean(ExecutorService.class)
    @ConditionalOnProperty(prefix = "huron.storage.rocketmq", name = "enable", havingValue = "true")
    public class RocketmqStorageConfiguration {

        @Bean(initMethod = "init")
        public RealtimeStorage rocketmqStorage() {

            log.info("=====================> init rocketmq-queue collector storage <=====================");

            RocketStorage storage = new RocketStorage();

            storage.setMappers(realtimeMappers);

            return storage;
        }

        private MQProducer producer() throws MQClientException {
            DefaultMQProducer producer = new DefaultMQProducer("");
            producer.setNamesrvAddr("");
            producer.start();
            return producer;
        }

        private MQConsumer consumer() throws MQClientException {
            DefaultMQPushConsumer consumer = new DefaultMQPushConsumer();
            consumer.setNamesrvAddr("");
            consumer.start();
            return consumer;
        }
    }
}
