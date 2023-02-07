package cc.powind.huron.core.storage;

import cc.powind.huron.core.model.Realtime;
import cc.powind.huron.core.utils.QueueUtils;
import cc.powind.huron.core.utils.RealtimeRabbitmqQueue;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class RabbitmqStorage extends AbstractStorage {

    private ConnectionFactory connectionFactory;

    private RealtimeRabbitmqQueue rabbitmqQueue;

    private ObjectMapper mapper;

    private Channel channel;

    private ExecutorService executorService;

    private int minFetchSize = 1000;

    private int maxFetchSize = 10000;

    private long maxFetchWait = 3000L;

    public ConnectionFactory getConnectionFactory() {
        return connectionFactory;
    }

    public void setConnectionFactory(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    public RealtimeRabbitmqQueue getRabbitmqQueue() {
        return rabbitmqQueue;
    }

    public void setRabbitmqQueue(RealtimeRabbitmqQueue rabbitmqQueue) {
        this.rabbitmqQueue = rabbitmqQueue;
    }

    public ObjectMapper getMapper() {
        return mapper;
    }

    public void setMapper(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public ExecutorService getExecutorService() {
        return executorService;
    }

    public void setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
    }

    public int getMinFetchSize() {
        return minFetchSize;
    }

    public void setMinFetchSize(int minFetchSize) {
        this.minFetchSize = minFetchSize;
    }

    public int getMaxFetchSize() {
        return maxFetchSize;
    }

    public void setMaxFetchSize(int maxFetchSize) {
        this.maxFetchSize = maxFetchSize;
    }

    public long getMaxFetchWait() {
        return maxFetchWait;
    }

    public void setMaxFetchWait(long maxFetchWait) {
        this.maxFetchWait = maxFetchWait;
    }

    @Override
    protected void init() {

        this.initConnection();

        this.initAsync();

        this.initRabbitmq();
    }

    protected void initConnection() {
        try {
            this.channel = connectionFactory.newConnection().createChannel();
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }

    protected void initAsync() {

        this.setAsync(new Async() {

            @Override
            public void submit(Realtime realtime) {
                produce(realtime);
            }

            @Override
            public void exec(List<Realtime> realtimeList) {
                doStore(realtimeList);
            }
        });
    }

    private void initRabbitmq() {

        executorService.submit(() -> {

            String[] queues = rabbitmqQueue.storageQueue();

            while (true) {

                if (queues == null || queues.length == 0) {

                    try {
                        log.info("--------  detect rabbitmq storage topics --------");
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException e) {
                        log.error(e);
                    }

                    queues = rabbitmqQueue.storageQueue();

                } else {
                    break;
                }
            }

            createQueue(queues);
        });
    }

    private void createQueue(String[] queues) {

        try {

            Channel channel = getChannel();

            for (String queue : queues) {

                // 交换机
                channel.exchangeDeclare(rabbitmqQueue.getStorageExchangeName(), rabbitmqQueue.getExchangeType(), true, false, false, null);

                // 队列
                channel.queueDeclare(queue, true, false, false, null);

                // 绑定
                channel.queueBind(queue, rabbitmqQueue.getStorageExchangeName(), queue);

                // 订阅
                channel.basicConsume(queue, true, new RealtimeConsumer(channel));
            }
        } catch (IOException e) {
            log.error("create realtime queue error", e);
        }
    }

    private void produce(Realtime realtime) {

        try {
            getChannel().basicPublish(rabbitmqQueue.getStorageExchangeName(), rabbitmqQueue.storageQueue(realtime), null, mapper.writeValueAsBytes(realtime));
        } catch (IOException ioe) {
            // xx
            ioe.printStackTrace();
        }

    }

    private Channel getChannel() {

        if (channel == null || !channel.isOpen()) {
            try {
                this.channel = connectionFactory.newConnection().createChannel();
            } catch (IOException | TimeoutException e) {
                log.error("create channel error", e);
            }
        }

        return this.channel;
    }

    public class RealtimeConsumer extends DefaultConsumer {

        private final BlockingQueue<Realtime> queue = new ArrayBlockingQueue<>(2 << 16);

        public RealtimeConsumer(Channel channel) {
            super(channel);
            this.init();
        }

        public void init() {

            List<Realtime> collect = new ArrayList<>();

            executorService.submit(() -> {

                //noinspection InfiniteLoopStatement
                while (true) {

                    try {

                        collect.clear();

                        int length = QueueUtils.drainTo(collect, this.queue, minFetchSize, maxFetchSize, maxFetchWait);

                        log.info(" ================>  fetch data size: " + length + "  <================== ");

                        getAsync().exec(collect);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        @Override
        public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {

            String routingKey = envelope.getRoutingKey();

            // getChannel().basicAck(envelope.getDeliveryTag(), false);

            Realtime realtime = mapper.readValue(body, rabbitmqQueue.getClazzFromRouteKey(routingKey));

            try {
                queue.offer(realtime, 3000, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
