package cc.powind.huron.basic.config;

import cc.powind.huron.core.model.Realtime;

import java.time.Instant;

public class MetricRealtime implements Realtime {

    private String metricId;

    private Instant time;

    private Double value;

    public String getMetricId() {
        return metricId;
    }

    public void setMetricId(String metricId) {
        this.metricId = metricId;
    }

    public void setTime(Instant time) {
        this.time = time;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    @Override
    public String getObjectId() {
        return metricId;
    }

    @Override
    public Instant getTime() {
        return time;
    }

    @Override
    public String getRealtimeId() {
        return metricId + "_" + time.toEpochMilli();
    }

    @Override
    public String toString() {
        return "Metric{" +
                "metricId='" + metricId + '\'' +
                ", time=" + time +
                ", value=" + value +
                '}';
    }
}
