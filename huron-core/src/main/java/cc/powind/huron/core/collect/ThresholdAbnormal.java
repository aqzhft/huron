package cc.powind.huron.core.collect;

import cc.powind.huron.core.model.AbstractAbnormal;
import cc.powind.huron.core.model.Metric;

public class ThresholdAbnormal extends AbstractAbnormal {

    private Metric metric;

    private ThresholdPolicy policy;

    public ThresholdAbnormal() {
    }

    public ThresholdAbnormal(Metric metric, ThresholdPolicy policy) {
        super(createDefaultMessage(metric, policy));
        this.metric = metric;
        this.policy = policy;
    }

    public ThresholdAbnormal(String message, Metric metric, ThresholdPolicy policy) {
        super(message);
        this.metric = metric;
        this.policy = policy;
    }

    public Metric getMetric() {
        return metric;
    }

    public void setMetric(Metric metric) {
        this.metric = metric;
    }

    public ThresholdPolicy getPolicy() {
        return policy;
    }

    public void setPolicy(ThresholdPolicy policy) {
        this.policy = policy;
    }

    @Override
    public String getObjectId() {
        return null;
    }

    @Override
    public String getRealtimeId() {
        return null;
    }

    protected static String createDefaultMessage(Metric metric, ThresholdPolicy policy) {
        return "policy [" + policy.getPolicyId() + "] [] is triggered, metric id is ";
    }
}
