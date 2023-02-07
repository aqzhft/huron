package cc.powind.huron.core.model;

public interface MetricHandler {

    /**
     * 度量数据的处理
     *
     * @param metric 指标
     */
    void handle(Metric metric);

    /**
     * 判断是否支持此metric处理
     *
     * @param metric 指标
     * @return bool
     */
    boolean isSupport(Metric metric);
}
