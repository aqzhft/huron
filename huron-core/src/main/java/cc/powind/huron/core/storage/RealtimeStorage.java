package cc.powind.huron.core.storage;

import cc.powind.huron.core.model.Realtime;

public interface RealtimeStorage {

    /**
     * Persistence real-time data
     *
     * @param realtime realtime
     */
    void store(Realtime realtime);
}
