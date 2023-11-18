package cc.powind.huron.basic.config;

import cc.powind.huron.assemble.properties.HuronProperties;
import cc.powind.huron.core.model.*;
import cc.powind.huron.rectifier.BlockingQueueRectifier;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collection;
import java.util.Collections;

@Configuration
@EnableConfigurationProperties(HuronProperties.class)
public class MyConfig {

    private final Log log = LogFactory.getLog(getClass());

    @Autowired
    private ObjectMapper mapper;

    @Bean
    public RealtimeMapper realtimeMapper() {
        return new RealtimeMapper() {

            @Override
            public boolean isSupport(Realtime realtime) {
                return true;
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

    public static class MetricHandlerImpl extends BlockingQueueRectifier<Metric> implements MetricHandler {

        public MetricHandlerImpl() {
            super.outflow((list) -> {
                System.out.println("config metric handler: callback");
            });
        }

        @Override
        public void handle(Metric metric) {
            super.inflow(metric);
        }

        @Override
        public boolean isSupport(Metric metric) {
            return BaseMetric.class.equals(metric.getClass());
        }

        // send to websocket
    }
}
