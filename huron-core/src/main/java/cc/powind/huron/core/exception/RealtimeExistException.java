package cc.powind.huron.core.exception;

import cc.powind.huron.core.model.Realtime;
import cc.powind.huron.core.model.RealtimeException;

public class RealtimeExistException extends RealtimeException {

    public RealtimeExistException(Realtime realtime) {
        super(realtime);
    }

}
