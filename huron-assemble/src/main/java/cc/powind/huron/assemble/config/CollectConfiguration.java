package cc.powind.huron.assemble.config;

import cc.powind.huron.core.collect.CollectService;
import cc.powind.huron.core.collect.CollectServiceImpl;
import cc.powind.huron.core.model.*;
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

    /**
     * 数据采集的核心类
     *
     * @return collect service
     */
    @Bean
    public CollectService collectService() {

        CollectServiceImpl impl = new CollectServiceImpl();

        // 过滤器
        impl.setFilters(filters);

        // 自定义校验器
        impl.setCustomValidators(customValidators);

        // 指标提取器
        impl.setDetectors(detectors);

        // 指标处理器
        impl.setMetricHandlers(metricHandlers);

        // 实时数据存储器
        impl.setStorages(storages);

        return impl;
    }
}
