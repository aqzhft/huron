package cc.powind.huron.basic.vm;

import cc.powind.huron.basic.config.MetricRealtime;
import cc.powind.huron.basic.config.UsageRealtime;
import cc.powind.huron.core.collect.BaseThresholdPolicy;
import cc.powind.huron.core.collect.ThresholdPolicy;
import cc.powind.huron.core.collect.ThresholdPolicyService;
import cc.powind.huron.core.model.DefaultRealtimeRegister;
import cc.powind.huron.core.model.RealtimeRegister;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class VmConfiguration {

    // CREATE TABLE IF NOT EXISTS cpu_memory_record (unique_id String, total UInt64, free UInt64, used UInt64, buff UInt64, us Float64, sy Float64, id Float64, time Datetime) ENGINE = MergeTree() PARTITION BY toYYYYMM(time) ORDER BY (unique_id, time);

    // CREATE TABLE IF NOT EXISTS abnormal_threshold (policy_id String, policy_name String, metric_id String, metric_name String, value Float64, threshold Float64, source_id String, time Datetime) ENGINE = MergeTree() PARTITION BY toYYYYMM(time) ORDER BY (policy_id, time);

    @Bean
    @ConditionalOnBean(DataSource.class)
    public CpuMemoryMapper cpuMemoryMapper(DataSource dataSource) {
        return new CpuMemoryMapper(dataSource);
    }

    @Bean
    public CpuMemoryDetector cpuMemoryDetector() {
        return new CpuMemoryDetector();
    }

    @Bean
    @ConditionalOnBean(DataSource.class)
    public ThresholdAbnormalHandler thresholdAbnormalHandler(DataSource dataSource) {
        return new ThresholdAbnormalHandler(dataSource);
    }

    /*@Bean(initMethod = "init")
    public MetricPersistenceMapper metricPersistenceMapper(DataSource dataSource) {
        BlockingQueueRectifier<BaseMetric> rectifier = new BlockingQueueRectifier<>();
        rectifier.setName("metricPersistenceMapperRectifier");
        MetricPersistenceMapper mapper = new MetricPersistenceMapper();
        mapper.setDataSource(dataSource);
        mapper.setRectifier(rectifier);
        return mapper;
    }*/

    @Bean
    public RealtimeRegister realtimeRegister() {
        return new DefaultRealtimeRegister() {
            @Override
            protected void init() {
                super.register("metric", MetricRealtime.class);
                super.register("usage", UsageRealtime.class);
                super.register("cpuMemory", CpuMemory.class);
            }
        };
    }

    @Bean
    public ThresholdPolicyService thresholdPolicyService() {
        return metricId -> {

            List<ThresholdPolicy> policyList = new ArrayList<>();

            {
                BaseThresholdPolicy policy = new BaseThresholdPolicy();
                policy.setPolicyId("CPU_LIMIT_WARN");
                policy.setPolicyName("CPU超频预警");
                policy.setMetricId("vm_cpu_used_abe");
                policy.setThreshold(10d);
                policy.setType("upper");
                policyList.add(policy);
            }

            {
                BaseThresholdPolicy policy = new BaseThresholdPolicy();
                policy.setPolicyId("MEMORY_LIMIT_WARN");
                policy.setPolicyName("内存溢出预警");
                policy.setMetricId("vm_memory_used_abe");
                policy.setThreshold(10d);
                policy.setType("upper");
                policyList.add(policy);
            }

            return policyList;
        };
    }
}
