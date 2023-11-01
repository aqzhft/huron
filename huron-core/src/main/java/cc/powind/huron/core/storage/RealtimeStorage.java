package cc.powind.huron.core.storage;

import cc.powind.huron.core.model.Realtime;

public interface RealtimeStorage {

    /**
     * Persistence real-time data
     *
     * @param realtime Real-time
     */
    void store(Realtime realtime);
}
