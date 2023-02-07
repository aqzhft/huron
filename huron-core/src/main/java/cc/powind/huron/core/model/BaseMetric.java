package cc.powind.huron.core.model;

import java.time.Instant;

public class BaseMetric implements Metric {

    private String metricId;

    private Instant time;

    private Double value;

    private Realtime source;

    @Override
    public String getMetricId() {
        return metricId;
    }

    public void setMetricId(String metricId) {
        this.metricId = metricId;
    }

    @Override
    public Instant getTime() {
        return time;
    }

    public void setTime(Instant time) {
        this.time = time;
    }

    @Override
    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    @Override
    public Realtime getSource() {
        return source;
    }

    public void setSource(Realtime source) {
        this.source = source;
    }
}
