package cc.powind.huron.assemble.config;

import cc.powind.huron.core.collect.CollectService;
import cc.powind.huron.core.model.RealtimeRegister;
import cc.powind.huron.core.router.NettyRouter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RealtimeRouterConfiguration {

    @Bean(initMethod = "start")
    @ConditionalOnMissingBean
    public NettyRouter nettyRouter(ObjectMapper objectMapper, CollectService collectService, RealtimeRegister register) {
        NettyRouter router = new NettyRouter();
        router.setMapper(objectMapper);
        router.setCollectService(collectService);
        router.setRegister(register);
        return router;
    }
}
