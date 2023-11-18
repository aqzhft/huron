package cc.powind.huron.core.collect;

import cc.powind.huron.core.model.Metric;
import cc.powind.huron.core.model.MetricHandler;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ThresholdValidateHandler implements MetricHandler {

    private ThresholdPolicyService policyService;

    private List<AbnormalHandler> abnormalHandlers;

    public ThresholdPolicyService getPolicyService() {
        return policyService;
    }

    public void setPolicyService(ThresholdPolicyService policyService) {
        this.policyService = policyService;
    }

    public List<AbnormalHandler> getAbnormalHandlers() {
        return abnormalHandlers;
    }

    public void setAbnormalHandlers(List<AbnormalHandler> abnormalHandlers) {
        this.abnormalHandlers = abnormalHandlers;
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
                if (policy.getMetricId().equals(metric.getMetricId()) && (metric.getValue().compareTo(policy.getThreshold()) * policy.getCompare() > 0)) {
                    handleAbnormal(createAbnormal(policy, metric));
                    if (!policy.getProceed()) {
                        break;
                    }
                }
            }
        }
    }

    protected void handleAbnormal(ThresholdAbnormal abnormal) {

        if (abnormalHandlers == null || abnormalHandlers.isEmpty()) {
            return;
        }

        for (AbnormalHandler handler : abnormalHandlers) {
            if (handler.isSupport(abnormal)) {
                handler.handle(abnormal);
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
