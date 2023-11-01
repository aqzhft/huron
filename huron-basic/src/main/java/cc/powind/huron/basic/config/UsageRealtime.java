package cc.powind.huron.basic.config;

import cc.powind.huron.core.model.Realtime;

import java.time.Instant;

public class UsageRealtime implements Realtime {

    private String equipmentId;

    private String indicatorId;

    private Double used;

    private Double total;

    private Instant time;

    public String getEquipmentId() {
        return equipmentId;
    }

    public void setEquipmentId(String equipmentId) {
        this.equipmentId = equipmentId;
    }

    public String getIndicatorId() {
        return indicatorId;
    }

    public void setIndicatorId(String indicatorId) {
        this.indicatorId = indicatorId;
    }

    public Double getUsed() {
        return used;
    }

    public void setUsed(Double used) {
        this.used = used;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    @Override
    public String getObjectId() {
        return equipmentId + "_" + indicatorId;
    }

    @Override
    public Instant getTime() {
        return time;
    }

    @Override
    public String getRealtimeId() {
        return getObjectId() + "_" + time.toEpochMilli();
    }

    public void setTime(Instant time) {
        this.time = time;
    }
}
