package cc.powind.huron.core.model;

import cc.powind.huron.core.exception.RealtimeValidateException;

public interface RealtimeValidator {

    RealtimeError validate(Realtime realtime);

    boolean isSupport(Realtime realtime);
}
