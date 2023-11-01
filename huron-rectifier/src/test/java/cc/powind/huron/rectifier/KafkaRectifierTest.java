package cc.powind.huron.rectifier;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.KafkaAdminClient;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.Test;

import java.util.Properties;
import java.util.concurrent.TimeUnit;

public class KafkaRectifierTest {

    @Test
    public void test() throws Exception {

        // objectMapper
        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.configure(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS, false);
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true);
        mapper.configure(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS, false);

        KafkaRectifier<String> rectifier = new KafkaRectifier<>();
        rectifier.setObjectMapper(mapper);
        rectifier.setAdminClient(adminClient());
        rectifier.setConsumer(consumer());
        rectifier.setProducer(producer());
        rectifier.setTopicMappings(topicMappings());

        rectifier.init();

        rectifier.outflow(strings -> {
            System.out.println(" consume ====> " + strings);
        });

        rectifier.inflow("hello");
        rectifier.inflow("world");

        TimeUnit.SECONDS.sleep(10);
    }

    private AdminClient adminClient() {
        Properties props = new Properties();
        props.put("bootstrap.servers", "192.168.10.240:32264");
        return KafkaAdminClient.create(props);
    }

    private Consumer<String, String> consumer() {
        Properties props = new Properties();
        props.put("bootstrap.servers", "192.168.10.240:32264");
        props.put("group.id", "test_group_id");
        props.put("auto.offset.reset", "earliest");
        props.put("fetch.max.bytes", "1024000");
        props.put("fetch.min.bytes", "102400");
        props.put("max.poll.records", "500");   // Kafka默认是500
        props.put("fetch.max.wait.ms", "3000");
        return new KafkaConsumer<>(props, new StringDeserializer(), new StringDeserializer());
    }

    private Producer<String, String> producer() {
        Properties props = new Properties();
        props.put("bootstrap.servers", "192.168.10.240:32264");
        props.put("acks", "0");
        return new KafkaProducer<>(props, new StringSerializer(), new StringSerializer());
    }

    private TopicMappings<String> topicMappings() {
        return new TopicMappings<String>() {
            @Override
            public String[] topics() {
                return new String[] {"test_rectifier"};
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