package cc.powind.huron.core.model;

import java.util.Collection;

public interface MetricDetector {

    /**
     * 从实时数据中提取指标
     *
     * @param realtime 实时数据
     * @return metric list
     */
    Collection<? extends Metric> detect(Realtime realtime);

    boolean isSupport(Realtime realtime);
}
