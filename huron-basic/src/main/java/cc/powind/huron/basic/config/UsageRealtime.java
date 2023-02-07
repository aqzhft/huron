package cc.powind.huron.basic.config;

import cc.powind.huron.core.model.Realtime;
import org.apache.commons.lang3.StringUtils;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

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

    @Override
    public Map<String, String> validate() {

        Map<String, String> error = new HashMap<>();

        if (StringUtils.isBlank(equipmentId)) {
            error.put("equipmentId", "equipment must not null");
        }

        if (StringUtils.isBlank(indicatorId)) {
            error.put("indicatorId", "indicatorId must not null");
        }

        if (total == null) {
            error.put("total", "total must not null");
        }

        if (used == null) {
            error.put("used", "used must not null");
        }

        if (time == null) {
            error.put("time", "time must not null");
        }

        return error;
    }
}
