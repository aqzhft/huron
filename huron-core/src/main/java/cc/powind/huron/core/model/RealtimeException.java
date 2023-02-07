package cc.powind.huron.core.model;

/**
 *
 * 实时数据异常
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
