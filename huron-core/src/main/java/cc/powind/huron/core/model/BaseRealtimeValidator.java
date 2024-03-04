package cc.powind.huron.core.model;

import cc.powind.huron.core.exception.RealtimeValidateException;

import java.util.ArrayList;
import java.util.List;

public class BaseRealtimeValidator implements RealtimeValidator {

    @Override
    public RealtimeError validate(Realtime realtime) {

        if (realtime == null) {
            return new RealtimeError(null, "realtime data is null");
        }

        List<RealtimeError.Error> errors = new ArrayList<>();

        if (realtime.getTime() == null) {
            errors.add(new RealtimeError.Error("time", "time is null"));
        }

        if (realtime.getRealtimeId() == null || "".equals(realtime.getRealtimeId())) {
            errors.add(new RealtimeError.Error("realtimeId", "realtime unique id is null"));
        }

        if (realtime.getObjectId() == null || "".equals(realtime.getObjectId())) {
            errors.add(new RealtimeError.Error("objectId", "realtime object id is null"));
        }

        return new RealtimeError(realtime, errors);
    }

    @Override
    public boolean isSupport(Realtime realtime) {
        return true;
    }
}
