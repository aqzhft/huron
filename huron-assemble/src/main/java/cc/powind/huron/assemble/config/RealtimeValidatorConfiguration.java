package cc.powind.huron.assemble.config;

import cc.powind.huron.core.model.BaseRealtimeValidator;
import cc.powind.huron.core.model.RealtimeValidator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RealtimeValidatorConfiguration {

    @Bean
    @ConditionalOnMissingBean(RealtimeValidator.class)
    public RealtimeValidator baseRealtimeValidator() {
        return new BaseRealtimeValidator();
    }
}
