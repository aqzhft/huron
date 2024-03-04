package cc.powind.huron.core.model;

import cc.powind.huron.core.exception.RealtimeExistException;

public interface RealtimeFilter {

    /**
     * Whether this real-time is existed
     *
     * @param realtime Real-time
     */
    void exist(Realtime realtime) throws RealtimeExistException;
}
