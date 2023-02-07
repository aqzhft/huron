package cc.powind.huron.assemble.config;

import cc.powind.huron.assemble.properties.HuronProperties;
import cc.powind.huron.core.collect.CollectService;
import cc.powind.huron.core.model.RealtimeRegister;
import cc.powind.huron.core.router.HttpRouter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class RealtimeRouterConfiguration {

    protected final Log log = LogFactory.getLog(getClass());

    @Autowired
    private HuronProperties properties;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private CollectService collectService;

    @Autowired
    private RealtimeRegister realtimeRegister;

    @Bean
    @ConditionalOnProperty(prefix = "huron.router.http", name = "enable", havingValue = "true")
    public ServletRegistrationBean<HttpRouter> servletRegistrationBean() {

        log.info("=====================> init http collector router <=====================");

        HttpRouter httpRouter = new HttpRouter();
        httpRouter.setCollectService(collectService);
        httpRouter.setRealtimeRegister(realtimeRegister);
        httpRouter.setMapper(mapper);

        ServletRegistrationBean<HttpRouter> registrationBean = new ServletRegistrationBean<>();
        registrationBean.setServlet(httpRouter);
        registrationBean.addUrlMappings(properties.getRouter().getHttp().getUrlPattern());
        return registrationBean;
    }
}
