package cc.powind.huron.core.exception;

import cc.powind.huron.core.model.Realtime;
import cc.powind.huron.core.model.RealtimeException;

public class RealtimeExistException extends RealtimeException {

    private Realtime realtime;

    public RealtimeExistException(Realtime realtime) {
        this.realtime = realtime;
    }

    public Realtime getRealtime() {
        return realtime;
    }

    public void setRealtime(Realtime realtime) {
        this.realtime = realtime;
    }
}
