package cc.powind.huron.core.storage;

import cc.powind.huron.core.exception.RealtimeStoreException;
import cc.powind.huron.core.model.Realtime;

import java.util.List;

public interface RealtimeStorage {

    /**
     * Enter storage process
     *
     * @param realtime Real-time
     */
    void store(Realtime realtime) throws RealtimeStoreException;

    <T extends Realtime> void store(List<T> realtimeList) throws RealtimeStoreException;
}
