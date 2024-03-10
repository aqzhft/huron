package cc.powind.huron.core.collect;

import cc.powind.huron.core.model.Realtime;
import cc.powind.huron.core.model.RealtimeWrapper;

public interface ExtractorRecorder {

    <T extends Realtime> void record(RealtimeWrapper<T> wrapper, String realtimeAlias);
}
