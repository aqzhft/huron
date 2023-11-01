package cc.powind.huron.rectifier;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.rabbitmq.client.ConnectionFactory;
import org.junit.Test;

public class RabbitRectifierTest {

    @Test
    public void test() throws Exception {

        // objectMapper
        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.configure(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS, false);
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true);
        mapper.configure(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS, false);

        RabbitRectifier<String> rectifier = new RabbitRectifier<>();
        rectifier.setName("rabbit");
        rectifier.setObjectMapper(mapper);
        rectifier.setTopicMappings(topicMappings());
        rectifier.setConnectionFactory(connectionFactory());

        rectifier.init();

        rectifier.outflow(strings -> {
            System.out.println(" consume ====> " + strings);
        });

        rectifier.inflow("hello");
        rectifier.inflow("world");

        /*new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                rectifier.inflow("hello" + RandomUtils.nextInt(1, 100));
                System.out.println("---> " + i);
            }

            System.out.println("数据发送完毕");
        }).start();*/

        System.in.read();
    }

    private ConnectionFactory connectionFactory() {

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("192.168.10.240");
        factory.setPort(29423);
        factory.setUsername("admin");
        factory.setPassword("gem123456");
        factory.setVirtualHost("/");

        return factory;
    }

    private TopicMappings<String> topicMappings() {
        return new TopicMappings<String>() {
            @Override
            public String[] topics() {
                return new String[]{"test_rectifier"};
            }

            @Override
            public String topic(String s) {
                return "test_rectifier";
            }

            @Override
            public Class<String> clazz(String topic) {
                return String.class;
            }
        };
    }

}