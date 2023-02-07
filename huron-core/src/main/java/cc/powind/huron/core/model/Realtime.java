package cc.powind.huron.core.model;

import java.time.Instant;
import java.util.Map;

/**
 * 实时数据
 */
public interface Realtime {

    /**
     * 监控点位标识
     *
     * @return objectId
     */
    String getObjectId();

    /**
     * 实时数据发生的时间
     *
     * @return time
     */
    Instant getTime();

    /**
     * 实时数据的唯一标识
     *
     * @return realtimeId
     */
    String getRealtimeId();

    /**
     * 校验实时数据
     *
     * @return errText map
     */
    Map<String, String> validate();
}
