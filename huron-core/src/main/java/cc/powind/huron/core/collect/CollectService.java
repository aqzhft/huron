package cc.powind.huron.core.collect;

import cc.powind.huron.core.model.*;
import cc.powind.huron.core.storage.RealtimeStorage;

public interface CollectService {

    /**
     * General data collection
     *
     * 1、Basic data verification {@link RealtimeValidator}
     * The specific verification logic is defined by a specific data model
     *
     * 2、Data filtering {@link RealtimeFilter}
     * The collected data is mixed (perhaps sent repeatedly) and needs further filtering
     *
     * 3、Real time calculation (A very important logic)
     * Real-time data is a refection to real-world events, we need to extract metrics of different dimensions from it
     * The metrics is abstracted into concrete classes {@link Metric}
     * {@link MetricDetector}
     *
     * 4、Storage {@link RealtimeStorage}
     * Real-time data needs to be stored for a more in-depth summary analysis
     *
     * @param realtime realtime
     */
    void collect(Realtime realtime);
}
