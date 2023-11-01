package cc.powind.huron.core.collect;

import cc.powind.huron.core.model.Metric;
import cc.powind.huron.core.model.MetricHandler;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ThresholdValidateHandler implements MetricHandler {

    private ThresholdPolicyService policyService;

    public ThresholdPolicyService getPolicyService() {
        return policyService;
    }

    public void setPolicyService(ThresholdPolicyService policyService) {
        this.policyService = policyService;
    }

    @Override
    public void handle(Metric metric) {

        if (metric == null || metric.getMetricId() == null || policyService == null) {
            return;
        }

        List<ThresholdPolicy> policyList = policyService.load(metric.getMetricId());

        // sort and group
        Map<String, List<ThresholdPolicy>> policyMappings = policyList.stream().sorted(Comparator.comparing(ThresholdPolicy::getOrder)).collect(Collectors.groupingBy(ThresholdPolicy::getType));

        for (String type : policyMappings.keySet()) {

            List<ThresholdPolicy> policies = policyMappings.get(type);

            for (ThresholdPolicy policy : policies) {
                if (policy.getThreshold().compareTo(metric.getValue()) < 0) {
                    ThresholdAbnormal abnormal = createAbnormal(policy, metric);

                    if (!policy.proceed()) {
                        break;
                    }
                }
            }
        }
    }

    protected ThresholdAbnormal createAbnormal(ThresholdPolicy policy, Metric metric) {
        return new ThresholdAbnormal(metric, policy);
    }

    @Override
    public boolean isSupport(Metric metric) {
        return true;
    }
}
