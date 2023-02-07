package cc.powind.huron.assemble.config;

import cc.powind.huron.core.collect.ThresholdValidateHandler;
import cc.powind.huron.core.model.MetricHandler;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class MetricHandlerConfiguration {

    @Bean
    @ConditionalOnMissingBean(name = "thresholdValidateHandler")
    public MetricHandler thresholdValidateHandler() {
        return new ThresholdValidateHandler();
    }
}
