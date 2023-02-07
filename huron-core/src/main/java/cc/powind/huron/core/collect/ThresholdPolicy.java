package cc.powind.huron.core.collect;

public interface ThresholdPolicy {

    String getPolicyId();

    String getPolicyName();

    String getMetricId();

    Double getThreshold();

    int getOrder();

    String getType();
}
