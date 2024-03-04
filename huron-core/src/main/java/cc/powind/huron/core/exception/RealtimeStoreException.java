package cc.powind.huron.core.exception;

import cc.powind.huron.core.model.Realtime;
import cc.powind.huron.core.model.RealtimeException;

import java.util.ArrayList;
import java.util.List;

public class RealtimeStoreException extends RealtimeException {

    private final List<Realtime> realtimeList = new ArrayList<>();

    public RealtimeStoreException(Realtime realtime) {
        this.realtimeList.add(realtime);
    }

    public RealtimeStoreException(List<Realtime> realtimeList) {
        this.realtimeList.addAll(realtimeList);
    }

    public List<Realtime> getRealtimeList() {
        return realtimeList;
    }
}
