package cc.powind.huron.core.model;

import java.time.Instant;

public interface Metric {

    String getMetricId();

    Instant getTime();

    Double getValue();

    Realtime getSource();
}
