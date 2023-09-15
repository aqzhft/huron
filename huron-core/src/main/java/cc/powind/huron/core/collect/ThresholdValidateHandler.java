package cc.powind.huron.core.collect;

import cc.powind.huron.core.model.Metric;
import cc.powind.huron.core.model.MetricHandler;

import java.util.List;

public class ThresholdValidateHandler implements MetricHandler {

    private CollectService collectService;

    private ThresholdPolicyService thresholdPolicyService;

    public ThresholdPolicyService getThresholdPolicyService() {
        return thresholdPolicyService;
    }

    public void setThresholdPolicyService(ThresholdPolicyService thresholdPolicyService) {
        this.thresholdPolicyService = thresholdPolicyService;
    }

    @Override
    public void handle(Metric metric) {

        if (metric == null || metric.getMetricId() == null || thresholdPolicyService == null) {
            return;
        }

        List<ThresholdPolicy> thresholdPolicyList = thresholdPolicyService.load(metric.getMetricId());

        // todo group sort

        for (ThresholdPolicy policy : thresholdPolicyList) {
            if (policy.getThreshold().compareTo(metric.getValue()) < 0) {
                collectService.collect(createAbnormal(policy, metric));
            }
        }
    }

    private ThresholdAbnormal createAbnormal(ThresholdPolicy policy, Metric metric) {
        return null;
    }

    @Override
    public boolean isSupport(Metric metric) {
        return true;
    }
}
