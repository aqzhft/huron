package cc.powind.huron.core.storage;

import org.apache.rocketmq.client.ClientConfig;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.MQConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.impl.MQAdminImpl;
import org.apache.rocketmq.client.impl.MQClientAPIImpl;
import org.apache.rocketmq.client.impl.factory.MQClientInstance;
import org.apache.rocketmq.client.producer.MQProducer;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;

import java.nio.charset.StandardCharsets;
import java.util.List;

public class RocketStorage extends AbstractStorage {

    private MQProducer producer;

    private MQConsumer consumer;

    @Override
    protected void init() {

        Message message = new Message("", "hello world".getBytes(StandardCharsets.UTF_8));

        // producer.send(message);

        producer.shutdown();
    }

    private void createTopics() {
        MQAdminImpl admin = new MQAdminImpl(new MQClientInstance(null, 1, "xx"));
        // admin.createTopic("", "", 1);
    }

    private void createConsumer() {

        DefaultMQPushConsumer currentConsumer = (DefaultMQPushConsumer) consumer;


        // currentConsumer.subscribe("", "*");
        currentConsumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {

                for (MessageExt ext : msgs) {
                    new String(ext.getBody(), StandardCharsets.UTF_8);
                }

                return null;
            }
        });


        ClientConfig clientConfig = currentConsumer.cloneClientConfig();
        String clientId = currentConsumer.buildMQClientId();
        MQClientInstance instance = new MQClientInstance(clientConfig, 0, clientId);
        // instance.start();

        MQClientAPIImpl apiImpl = instance.getMQClientAPIImpl();
    }
}
