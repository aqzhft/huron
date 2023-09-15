package cc.powind.huron.core.storage;

import cc.powind.huron.core.model.Realtime;
import cc.powind.huron.core.utils.RealtimeKafkaTopic;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class KafkaStorage extends AbstractStorage {

    /**
     * Maximum waiting time for pulling data
     */
    private long maxFetchWait = 3000L;

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
    private Producer<String, Realtime> producer;

    /**
     * Message consumer
     */
    private Consumer<String, Realtime> consumer;

    /**
     * Thread pool
     */
    private ExecutorService executorService;

    /**
     * Real-time topic
     */
    private RealtimeKafkaTopic kafkaTopic;

    public long getMaxFetchWait() {
        return maxFetchWait;
    }

    public void setMaxFetchWait(long maxFetchWait) {
        this.maxFetchWait = maxFetchWait;
    }

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

    public Producer<String, Realtime> getProducer() {
        return producer;
    }

    public void setProducer(Producer<String, Realtime> producer) {
        this.producer = producer;
    }

    public Consumer<String, Realtime> getConsumer() {
        return consumer;
    }

    public void setConsumer(Consumer<String, Realtime> consumer) {
        this.consumer = consumer;
    }

    public ExecutorService getExecutorService() {
        return executorService;
    }

    public void setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
    }

    public RealtimeKafkaTopic getKafkaTopic() {
        return kafkaTopic;
    }

    public void setKafkaTopic(RealtimeKafkaTopic kafkaTopic) {
        this.kafkaTopic = kafkaTopic;
    }

    @Override
    protected void init() {

        this.initAsync();

        this.initKafka();
    }

    private void initAsync() {

        this.setAsync(new Async() {

            @Override
            public void submit(Realtime realtime) {
                producer.send(new ProducerRecord<>(kafkaTopic.getStorageTopic(realtime), realtime));
            }

            @Override
            public void exec(List<Realtime> realtimeList) {
                doStore(realtimeList);
            }
        });
    }

    private void initKafka() {

        executorService.submit(() -> {

            String[] topics = kafkaTopic.getStorageTopic();

            while (true) {

                if (topics == null || topics.length == 0) {

                    try {
                        log.info("--------  detect kafka storage topics --------");
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException e) {
                        log.error(e);
                    }

                    topics = kafkaTopic.getStorageTopic();

                } else {
                    break;
                }
            }

            this.createTopics(topics);

            this.subscribe(topics);

            this.initConsumeThread();
        });
    }

    private void createTopics(String[] topics) {

        if (topics == null || topics.length == 0) {
            throw new IllegalArgumentException("storage topics is empty");
        }

        List<NewTopic> newTopics = Arrays.stream(topics).map(topic -> new NewTopic(topic, numPartition, replicationFactor)).collect(Collectors.toList());

        adminClient.createTopics(newTopics);

        adminClient.close();
    }

    private void subscribe(String[] topics) {

        if (topics == null || topics.length == 0) {
            throw new IllegalArgumentException("storage topics is empty");
        }

        consumer.subscribe(Arrays.asList(topics));
    }

    private void initConsumeThread() {

        executorService.submit(() -> {

            ConsumerRecords<String, Realtime> records;
            List<Realtime> realtimeList;

            //noinspection InfiniteLoopStatement
            while (true) {

                try {

                    records = consumer.poll(Duration.ofMillis(maxFetchWait));

                    log.info(" ================>  fetch data size: " + records.count() + "  <================== ");

                    realtimeList = new ArrayList<>();

                    for (ConsumerRecord<String, Realtime> record : records) {
                        if (record.value() != null) {
                            realtimeList.add(record.value());
                        }
                    }

                    this.getAsync().exec(realtimeList);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
