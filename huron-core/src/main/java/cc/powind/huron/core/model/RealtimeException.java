package cc.powind.huron.core.model;

/**
 *
 * Real-time exception base class
 *
 */
public class RealtimeException extends Exception {

    private Realtime realtime;

    public Realtime getRealtime() {
        return realtime;
    }

    public void setRealtime(Realtime realtime) {
        this.realtime = realtime;
    }

    public RealtimeException(Realtime realtime) {
        this.realtime = realtime;
    }
}
