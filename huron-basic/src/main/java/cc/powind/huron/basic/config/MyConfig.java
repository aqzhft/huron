package cc.powind.huron.basic.config;

import cc.powind.huron.core.model.*;
import cc.powind.huron.view.WebsocketConfig;
import cc.powind.huron.view.WebsocketServer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.Collections;

@Configuration
@Import(WebsocketConfig.class)
public class MyConfig {

    private final Log log = LogFactory.getLog(getClass());

    @Autowired
    private RealtimeRegister realtimeRegister;

    @Autowired
    private WebsocketServer websocketServer;

    @Autowired
    private ObjectMapper mapper;

    @PostConstruct
    public void initRegister() {

        realtimeRegister.register("metric", MetricRealtime.class);

        realtimeRegister.register("usage", UsageRealtime.class);
    }

    @Bean
    public RealtimeMapper realtimeMapper() {
        return new RealtimeMapper() {
            @Override
            public boolean isSupport(Realtime realtime) {
                return true;
            }

            @Override
            public void insert(Realtime realtime) {
                log.info("insert one realtime");
            }

            @Override
            public void insertBatch(Collection<Realtime> realtimeList) {
                log.info("insert multiple realtime [" + realtimeList.toString() + "]");
            }
        };
    }

    @Bean
    public MetricDetector metricRealtimeDetector() {
        return new MetricDetector() {
            @Override
            public Collection<? extends Metric> detect(Realtime realtime) {

                MetricRealtime metric = (MetricRealtime) realtime;

                BaseMetric baseMetric = new BaseMetric();
                baseMetric.setMetricId(metric.getObjectId());
                baseMetric.setTime(metric.getTime());
                baseMetric.setValue(metric.getValue());
                baseMetric.setSource(metric);

                return Collections.singletonList(baseMetric);
            }

            @Override
            public boolean isSupport(Realtime realtime) {
                return realtime.getClass().equals(MetricRealtime.class);
            }
        };
    }

    @Bean
    public MetricDetector usageRealtimeDetector() {
        return new MetricDetector() {
            @Override
            public Collection<? extends Metric> detect(Realtime realtime) {

                UsageRealtime metric = (UsageRealtime) realtime;

                BaseMetric baseMetric = new BaseMetric();
                baseMetric.setMetricId(metric.getObjectId());
                baseMetric.setTime(metric.getTime());
                baseMetric.setValue(metric.getUsed() / metric.getTotal() * 100);
                baseMetric.setSource(metric);

                return Collections.singletonList(baseMetric);
            }

            @Override
            public boolean isSupport(Realtime realtime) {
                return realtime.getClass().equals(UsageRealtime.class);
            }
        };
    }

    @Bean
    public MetricHandler metricHandler() {
        return new MetricHandlerImpl();
    }

    public class MetricHandlerImpl extends BlockingQueueAsync<Metric> implements MetricHandler {

        @Override
        public void handle(Metric metric) {
            super.submit(metric);
        }

        @Override
        public boolean isSupport(Metric metric) {
            return BaseMetric.class.equals(metric.getClass());
        }

        @Override
        public void exec(Collection<Metric> list) {
            try {
                websocketServer.send(mapper.writeValueAsString(list));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
    }
}
