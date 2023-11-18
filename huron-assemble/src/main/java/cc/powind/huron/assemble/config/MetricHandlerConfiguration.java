package cc.powind.huron.assemble.config;

import cc.powind.huron.core.collect.AbnormalHandler;
import cc.powind.huron.core.collect.ThresholdPolicyService;
import cc.powind.huron.core.collect.ThresholdValidateHandler;
import cc.powind.huron.core.model.MetricHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class MetricHandlerConfiguration {

    @Autowired(required = false)
    private ThresholdPolicyService policyService;

    @Autowired(required = false)
    private List<AbnormalHandler> abnormalHandlers;

    @Bean
    public MetricHandler thresholdValidateHandler() {
        ThresholdValidateHandler handler = new ThresholdValidateHandler();
        handler.setPolicyService(policyService);
        handler.setAbnormalHandlers(abnormalHandlers);
        return handler;
    }
}
