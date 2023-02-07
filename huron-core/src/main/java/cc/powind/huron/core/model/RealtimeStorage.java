package cc.powind.huron.core.model;

public interface RealtimeStorage {

    /**
     * 持久化实时数据
     *
     * @param realtime realtime
     */
    void store(Realtime realtime);
}
