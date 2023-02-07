package cc.powind.huron.basic.config;

import cc.powind.huron.core.model.Realtime;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

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
    public Map<String, String> validate() {

        Map<String, String> errText = new HashMap<>();

        if (metricId == null) {
            errText.put("metricId", "metricId is need");
        }

        if (time == null) {
            errText.put("time", "time is need");
        }

        if (value == null) {
            errText.put("value", "value must not empty");
        }

        return errText;
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
