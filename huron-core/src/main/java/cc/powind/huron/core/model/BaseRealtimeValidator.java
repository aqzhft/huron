package cc.powind.huron.core.model;

import cc.powind.huron.core.exception.RealtimeValidateException;

public class BaseRealtimeValidator implements RealtimeValidator {

    @Override
    public void validate(Realtime realtime) throws RealtimeValidateException {

        if (realtime == null) {
            throw new RealtimeValidateException(null, "realtime data is null");
        }

        if (realtime.getTime() == null) {
            throw new RealtimeValidateException(realtime, "time is null");
        }

        if (realtime.getRealtimeId() == null || "".equals(realtime.getRealtimeId())) {
            throw new RealtimeValidateException(realtime, "realtime unique id is null");
        }

        if (realtime.getObjectId() == null || "".equals(realtime.getObjectId())) {
            throw new RealtimeValidateException(realtime, "realtime object id is null");
        }
    }

    @Override
    public boolean isSupport(Realtime realtime) {
        return true;
    }
}
