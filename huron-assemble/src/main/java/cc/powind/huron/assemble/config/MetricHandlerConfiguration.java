package cc.powind.huron.assemble.config;

import cc.powind.huron.core.collect.ThresholdPolicyService;
import cc.powind.huron.core.collect.ThresholdValidateHandler;
import cc.powind.huron.core.model.MetricHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MetricHandlerConfiguration {

    @Bean
    public MetricHandler thresholdValidateHandler(@Autowired(required = false) ThresholdPolicyService policyService) {
        ThresholdValidateHandler handler = new ThresholdValidateHandler();
        handler.setPolicyService(policyService);
        return handler;
    }
}
