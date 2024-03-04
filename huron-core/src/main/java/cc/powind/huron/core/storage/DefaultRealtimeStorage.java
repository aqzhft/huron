package cc.powind.huron.core.storage;

import cc.powind.huron.core.exception.RealtimeStoreException;
import cc.powind.huron.core.model.Realtime;
import cc.powind.huron.core.model.RealtimeMapper;
import cc.powind.huron.rectifier.Rectifier;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;
import java.util.stream.Collectors;

public class DefaultRealtimeStorage implements RealtimeStorage {

    protected Log log = LogFactory.getLog(getClass());

    private Rectifier<Realtime> rectifier;

    private List<RealtimeMapper> mappers;

    public Rectifier<Realtime> getRectifier() {
        return rectifier;
    }

    public void setRectifier(Rectifier<Realtime> rectifier) {
        this.rectifier = rectifier;
        this.rectifier.outflow(this::doStore);
    }

    public List<RealtimeMapper> getMappers() {
        return mappers;
    }

    public void setMappers(List<RealtimeMapper> mappers) {
        this.mappers = mappers;
    }

    @Override
    public void store(Realtime realtime) throws RealtimeStoreException {
        try {
            rectifier.inflow(realtime);
        } catch (Exception e) {
            throw new RealtimeStoreException(realtime);
        }
    }

    @Override
    public <T extends Realtime> void store(List<T> realtimeList) throws RealtimeStoreException {
        List<Realtime> list = realtimeList.stream().map(realtime -> (Realtime) realtime)
                .collect(Collectors.toList());
        try {
            rectifier.inflow(list);
        } catch (Exception e) {
            throw new RealtimeStoreException(list);
        }
    }

    /**
     * The real storage operations
     *
     * @param realtimeList Real-time collection
     */
    protected void doStore(Collection<Realtime> realtimeList) {

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
    private Map<RealtimeMapper, List<Realtime>> groupRealtimeData(Collection<Realtime> realtimeList) {

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
}
