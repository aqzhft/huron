package cc.powind.huron.core.model;

import cc.powind.huron.core.exception.RealtimeValidateException;

public interface RealtimeValidator {

    void validate(Realtime realtime) throws RealtimeValidateException;

    boolean isSupport(Realtime realtime);
}
