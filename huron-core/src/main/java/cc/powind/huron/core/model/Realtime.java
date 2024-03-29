package cc.powind.huron.core.model;

import java.time.Instant;

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
}
