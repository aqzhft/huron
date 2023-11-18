package cc.powind.huron.basic.vm;

import cc.powind.huron.core.model.Realtime;
import org.apache.commons.lang3.StringUtils;

import java.time.Instant;

public class DiskInfo implements Realtime {

    private String uniqueId;

    private String path;

    private Long total;

    private Long used;

    private Instant time;

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public Long getUsed() {
        return used;
    }

    public void setUsed(Long used) {
        this.used = used;
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
