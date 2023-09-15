package cc.powind.huron.core.model;

import java.util.Collection;

public interface MetricDetector {

    Collection<? extends Metric> detect(Realtime realtime);

    boolean isSupport(Realtime realtime);
}
