package cc.powind.huron.core.collect;

public interface ThresholdPolicy {

    String getPolicyId();

    String getPolicyName();

    String getMetricId();

    Double getThreshold();

    Integer getOrder();

    String getType();

    Boolean getProceed();

    Integer getCompare();
}
