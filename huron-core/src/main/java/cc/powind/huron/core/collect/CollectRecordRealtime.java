package cc.powind.huron.core.collect;

import cc.powind.huron.core.model.Realtime;

import java.time.Instant;

public class CollectRecordRealtime implements Realtime {

    private Long total;

    private Long invalid;

    private Long exist;

    private Long other;

    private Long metrics;

    private Instant time;

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public Long getInvalid() {
        return invalid;
    }

    public void setInvalid(Long invalid) {
        this.invalid = invalid;
    }

    public Long getExist() {
        return exist;
    }

    public void setExist(Long exist) {
        this.exist = exist;
    }

    public Long getOther() {
        return other;
    }

    public void setOther(Long other) {
        this.other = other;
    }

    public Long getMetrics() {
        return metrics;
    }

    public void setMetrics(Long metrics) {
        this.metrics = metrics;
    }

    @Override
    public String getObjectId() {
        return "default_collect_record";
    }

    @Override
    public Instant getTime() {
        return time;
    }

    @Override
    public String getRealtimeId() {

        if (time != null) {
            return "default_collect_record_" + time.toEpochMilli();
        }

        return null;
    }

    public void setTime(Instant time) {
        this.time = time;
    }
}
