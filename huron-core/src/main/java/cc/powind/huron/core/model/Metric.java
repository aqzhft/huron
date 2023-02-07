package cc.powind.huron.core.model;

import java.time.Instant;

public interface Metric {

    /**
     * 指标编号
     *
     * @return metricId
     */
    String getMetricId();

    /**
     * 发生时间
     *
     * @return time
     */
    Instant getTime();

    /**
     * 度量值
     *
     * @return value
     */
    Double getValue();

    /**
     * 触发源
     *
     * @return realtime
     */
    Realtime getSource();
}
