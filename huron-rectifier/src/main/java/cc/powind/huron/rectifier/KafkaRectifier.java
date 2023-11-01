package cc.powind.huron.rectifier;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.ListTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.admin.TopicListing;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class KafkaRectifier <T> extends AbstractQueueRectifier <T> implements Runnable {

    /**
     * Partition number
     */
    private int numPartition = 2;

    /**
     * Replication number
     */
    private short replicationFactor = 0;

    /**
     * Kafka admin client
     */
    private AdminClient adminClient;

    /**
     * Message producer
     */
    private Producer<String, String> producer;

    /**
     * Message consumer
     */
    private Consumer<String, String> consumer;

    private TopicMappings<T> topicMappings;

    private ObjectMapper objectMapper;

    public int getNumPartition() {
        return numPartition;
    }

    public void setNumPartition(int numPartition) {
        this.numPartition = numPartition;
    }

    public short getReplicationFactor() {
        return replicationFactor;
    }

    public void setReplicationFactor(short replicationFactor) {
        this.replicationFactor = replicationFactor;
    }

    public AdminClient getAdminClient() {
        return adminClient;
    }

    public void setAdminClient(AdminClient adminClient) {
        this.adminClient = adminClient;
    }

    public Producer<String, String> getProducer() {
        return producer;
    }

    public void setProducer(Producer<String, String> producer) {
        this.producer = producer;
    }

    public Consumer<String, String> getConsumer() {
        return consumer;
    }

    public void setConsumer(Consumer<String, String> consumer) {
        this.consumer = consumer;
    }

    public TopicMappings<T> getTopicMappings() {
        return topicMappings;
    }

    public void setTopicMappings(TopicMappings<T> topicMappings) {
        this.topicMappings = topicMappings;
    }

    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    protected void offer(T t, long timeout, TimeUnit unit) {
        try {
            producer.send(new ProducerRecord<>(topicMappings.topic(t), objectMapper.writeValueAsString(t)));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    public void init() {

        String[] topics = topicMappings.topics();

        this.createTopics(topics);

        this.subscribe(topics);

        new Thread(this).start();
    }

    private void createTopics(String[] topics) {

        if (topics == null || topics.length == 0) {
            throw new IllegalArgumentException("storage topics is empty");
        }

        try {
            ListTopicsResult topicsResult = adminClient.listTopics();
            String[] existTopics = topicsResult.listings().get().stream().map(TopicListing::name).toArray(String[]::new);

            List<NewTopic> newTopics = Arrays.stream(topics)
                    .filter(topic -> !ArrayUtils.contains(existTopics, topic))
                    .map(topic -> new NewTopic(topic, numPartition, replicationFactor)).collect(Collectors.toList());

            adminClient.createTopics(newTopics);

            adminClient.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void subscribe(String[] topics) {

        if (topics == null || topics.length == 0) {
            throw new IllegalArgumentException("storage topics is empty");
        }

        consumer.subscribe(Arrays.asList(topics));
    }

    @Override
    public void run() {

        ConsumerRecords<String, String> records;
        List<T> list;

        //noinspection InfiniteLoopStatement
        while (true) {

            try {

                records = consumer.poll(Duration.ofMillis(getMaxFetchWait()));

                log.info(" ================>  fetch data size: " + records.count() + "  <================== ");

                list = new ArrayList<>();

                for (ConsumerRecord<String, String> record : records) {
                    if (record.value() != null) {

                        T t = objectMapper.readValue(record.value(), topicMappings.clazz(record.topic()));

                        list.add(t);
                    }
                }

                callback(list);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
