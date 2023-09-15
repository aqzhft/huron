package cc.powind.huron.assemble.config;

import cc.powind.huron.core.collect.CollectService;
import cc.powind.huron.core.collect.CollectServiceImpl;
import cc.powind.huron.core.model.MetricDetector;
import cc.powind.huron.core.model.MetricHandler;
import cc.powind.huron.core.model.RealtimeCustomValidator;
import cc.powind.huron.core.model.RealtimeFilter;
import cc.powind.huron.core.storage.RealtimeStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration(proxyBeanMethods = false)
public class CollectConfiguration {

    @Autowired(required = false)
    private List<RealtimeFilter> filters;

    @Autowired(required = false)
    private List<RealtimeCustomValidator> customValidators;

    @Autowired(required = false)
    private List<MetricDetector> detectors;

    @Autowired(required = false)
    private List<MetricHandler> metricHandlers;

    @Autowired(required = false)
    private List<RealtimeStorage> storages;

    @Bean
    public CollectService collectService() {

        CollectServiceImpl impl = new CollectServiceImpl();

        impl.setFilters(filters);

        impl.setCustomValidators(customValidators);

        impl.setDetectors(detectors);

        impl.setMetricHandlers(metricHandlers);

        impl.setStorages(storages);

        return impl;
    }
}
