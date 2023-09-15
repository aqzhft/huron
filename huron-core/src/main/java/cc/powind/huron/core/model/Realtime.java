package cc.powind.huron.core.model;

import java.time.Instant;
import java.util.Map;

public interface Realtime {

    /**
     * Object's unique id
     *
     * @return objectId
     */
    String getObjectId();

    /**
     * Real-time create time
     *
     * @return time
     */
    Instant getTime();

    /**
     * The Real-time data unique id
     *
     * @return realtimeId
     */
    String getRealtimeId();

    /**
     * validate Real-time
     *
     * @return errText map
     */
    Map<String, String> validate();
}
