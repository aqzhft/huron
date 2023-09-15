package cc.powind.huron.core.model;

public interface MetricHandler {

    void handle(Metric metric);

    boolean isSupport(Metric metric);
}
