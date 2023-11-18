package cc.powind.huron.basic.vm;

import cc.powind.huron.core.model.Realtime;
import org.apache.commons.lang3.StringUtils;

import java.time.Instant;

public class CpuMemory implements Realtime {

    private String uniqueId;

    private Long total;

    private Long free;

    private Long used;

    private Long buff;

    private Double us;

    private Double sy;

    private Double id;

    private Instant time;

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public Long getFree() {
        return free;
    }

    public void setFree(Long free) {
        this.free = free;
    }

    public Long getUsed() {
        return used;
    }

    public void setUsed(Long used) {
        this.used = used;
    }

    public Long getBuff() {
        return buff;
    }

    public void setBuff(Long buff) {
        this.buff = buff;
    }

    public Double getUs() {
        return us;
    }

    public void setUs(Double us) {
        this.us = us;
    }

    public Double getSy() {
        return sy;
    }

    public void setSy(Double sy) {
        this.sy = sy;
    }

    public Double getId() {
        return id;
    }

    public void setId(Double id) {
        this.id = id;
    }

    @Override
    public String getObjectId() {
        return uniqueId;
    }

    public Instant getTime() {
        return time;
    }

    @Override
    public String getRealtimeId() {

        if (StringUtils.isNotBlank(uniqueId) && time != null) {
            return uniqueId + "_" + time.getEpochSecond();
        }

        return null;
    }

    public void setTime(Instant time) {
        this.time = time;
    }
}
