package cc.powind.huron.core.model;

public interface RealtimeFilter {

    /**
     * Whether this real-time is existed
     *
     * @param realtime Real-time
     */
    void exist(Realtime realtime) throws RealtimeException;
}
