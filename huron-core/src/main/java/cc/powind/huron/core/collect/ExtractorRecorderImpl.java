package cc.powind.huron.core.collect;

import cc.powind.huron.core.model.Realtime;
import cc.powind.huron.core.model.RealtimeWrapper;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ExtractorRecorderImpl implements ExtractorRecorder {

    private final static Map<String, ExtractInfo> infoList = new ConcurrentHashMap<>(2 << 8);

    @Override
    public <T extends Realtime> void record(RealtimeWrapper<T> wrapper, String realtimeAlias) {

        ExtractInfo extractInfo = infoList.computeIfAbsent(wrapper.getExtractorId(), key -> new ExtractInfo(
                wrapper.getExtractorId(), wrapper.getExtractorName()
        ));

        // Flush record information
        extractInfo.flush(wrapper.getExtractorName(), realtimeAlias, wrapper.getIpAddress(), wrapper.getRealtimeList().size());
    }
}
