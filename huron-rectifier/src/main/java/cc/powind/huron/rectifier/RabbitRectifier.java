package cc.powind.huron.rectifier;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class RabbitRectifier<T> extends AbstractQueueRectifier<T> implements Runnable {

    private static final String DEFAULT_EXCHANGE_NAME = "rectifier";

    private TopicMappings<T> topicMappings;

    private ConnectionFactory connectionFactory;

    private ObjectMapper objectMapper;

    private RealtimeConsumer consumer;

    public TopicMappings<T> getTopicMappings() {
        return topicMappings;
    }

    public void setTopicMappings(TopicMappings<T> topicMappings) {
        this.topicMappings = topicMappings;
    }

    public ConnectionFactory getConnectionFactory() {
        return connectionFactory;
    }

    public void setConnectionFactory(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void init() {

        String[] topics = topicMappings.topics();

        createQueue(topics);

        subscribe(topics);

        new Thread(this).start();
    }

    protected void subscribe(String[] topics) {

        try {

            Channel channel = getConnectionFactory().newConnection().createChannel();

            for (String topic : topics) {
                channel.basicConsume(topic, false, createConsumer(channel));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private RealtimeConsumer createConsumer(Channel channel) {
        return this.consumer = new RealtimeConsumer(channel);
    }

    protected void createQueue(String[] topics) {

        try {

            Channel channel = getConnectionFactory().newConnection().createChannel();

            for (String queue : topics) {

                // define exchange
                channel.exchangeDeclare(getExchangeName(), BuiltinExchangeType.FANOUT, true, false, false, null);

                // define queue
                channel.queueDeclare(queue, true, false, false, null);

                // binding queue and exchange with routing key, queue and routing key use the same name
                channel.queueBind(queue, getExchangeName(), queue);
            }
        } catch (Exception e) {
            log.error("create realtime queue error", e);
        }
    }

    private void produce(T t) {

        try {
            Channel channel = getConnectionFactory().newConnection().createChannel();
            channel.confirmSelect();
            channel.basicPublish(getExchangeName(), topicMappings.topic(t), null, objectMapper.writeValueAsBytes(t));
        } catch (Exception ioe) {
            // xx
            ioe.printStackTrace();
        }
    }

    public class ValueWrapper {

        private final Long deliveryTag;

        private final Class<T> clazz;

        private final byte[] bytes;

        public ValueWrapper(Long deliveryTag, Class<T> clazz, byte[] bytes) {
            this.deliveryTag = deliveryTag;
            this.clazz = clazz;
            this.bytes = bytes;
        }

        public T getValue() {
            try {
                return objectMapper.readValue(bytes, clazz);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        public Long getDeliveryTag() {
            return deliveryTag;
        }
    }

    public class RealtimeConsumer extends DefaultConsumer {

        List<ValueWrapper> collect = new ArrayList<>(getMaxFetchSize());

        public final BlockingQueue<ValueWrapper> queue = new ArrayBlockingQueue<>(getMaxFetchSize());

        public RealtimeConsumer(Channel channel) {
            super(channel);
        }

        @Override
        public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
            String routingKey = envelope.getRoutingKey();
            queue.offer(new ValueWrapper(envelope.getDeliveryTag(), topicMappings.clazz(routingKey), body));
            // getChannel().basicAck(envelope.getDeliveryTag(), false);
        }

        @Override
        public void handleShutdownSignal(String consumerTag, ShutdownSignalException sig) {
            System.out.println("connection lost");
        }

        @Override
        public void handleRecoverOk(String consumerTag) {
            System.out.println("connect recover");
        }

        public List<T> fetch() {

            try {

                collect.clear();

                QueueUtils.drainTo(collect, queue, getMinFetchSize(), getMaxFetchSize(), getMaxFetchWait());

                if (!collect.isEmpty()) {
                    ValueWrapper valueWrapper = collect.get(collect.size() - 1);
                    getChannel().basicAck(valueWrapper.getDeliveryTag(), true);

                    return collect.stream().map(ValueWrapper::getValue).filter(Objects::nonNull).collect(Collectors.toList());
                }
            } catch (Exception e) {
                log.error(e);
            }

            return Collections.emptyList();
        }
    }

    protected String getExchangeName() {
        return getName() + "_" + DEFAULT_EXCHANGE_NAME;
    }

    @Override
    protected void offer(T t, long timeout, TimeUnit unit) {
        produce(t);
    }

    @Override
    public void run() {

        //noinspection InfiniteLoopStatement
        while (true) {

            List<T> dataList = consumer.fetch();

            if (!dataList.isEmpty()) {
                callback(dataList);
            }
        }
    }
}
