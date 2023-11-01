package cc.powind.huron.core.model;

import java.util.Collection;

public interface RealtimeMapper {

    boolean isSupport(Realtime realtime);

    /**
     * batch insert
     *
     * @param realtimeList Real-time collection
     */
    void insertBatch(Collection<Realtime> realtimeList);
}
