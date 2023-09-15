package cc.powind.huron.core.storage;

import cc.powind.huron.core.model.Realtime;
import cc.powind.huron.core.model.RealtimeMapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractStorage implements RealtimeStorage {

    protected Log log = LogFactory.getLog(getClass());

    private Async async;

    private List<RealtimeMapper> mappers;

    public Async getAsync() {
        return async;
    }

    public void setAsync(Async async) {
        this.async = async;
    }

    public List<RealtimeMapper> getMappers() {
        return mappers;
    }

    public void setMappers(List<RealtimeMapper> mappers) {
        this.mappers = mappers;
    }

    @Override
    public void store(Realtime realtime) {
        async.submit(realtime);
    }

    /**
     * The real storage operations
     *
     * @param realtimeList Real-time collection
     */
    protected void doStore(List<Realtime> realtimeList) {

        if (mappers == null || mappers.isEmpty() || realtimeList == null || realtimeList.isEmpty()) {
            return;
        }

        // Grouping based on mapper
        Map<RealtimeMapper, List<Realtime>> listMap = groupRealtimeData(realtimeList);

        for (RealtimeMapper mapper : listMap.keySet()) {
            doStore(mapper, listMap.get(mapper));
        }
    }

    /**
     * Grouping based on mapper
     *
     * @param realtimeList Real-time collection
     * @return Real-time group
     */
    private Map<RealtimeMapper, List<Realtime>> groupRealtimeData(List<Realtime> realtimeList) {

        Map<RealtimeMapper, List<Realtime>> map = new HashMap<>();

        for (Realtime realtime : realtimeList) {
            for (RealtimeMapper mapper : mappers) {
                if (mapper.isSupport(realtime)) {
                    map.computeIfAbsent(mapper, k -> new ArrayList<>()).add(realtime);
                }
            }
        }

        return map;
    }

    /**
     * The real storage operations
     *
     * @param mapper Persistence mapper
     * @param realtimeList Real-time collection
     */
    protected void doStore(RealtimeMapper mapper, List<Realtime> realtimeList) {

        if (mapper == null || realtimeList == null || realtimeList.isEmpty()) {
            return;
        }

        mapper.insertBatch(realtimeList);
    }

    protected abstract void init();

    protected interface Async {

        void submit(Realtime realtime);

        void exec(List<Realtime> realtimeList);
    }
}
