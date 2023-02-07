package cc.powind.huron.core.model;

/**
 * 实时数据 过滤器
 */
public interface RealtimeFilter {

    /**
     * 判断采集的实时数据是否已经存在
     *
     * @param realtime 实时数据
     */
    void exist(Realtime realtime) throws RealtimeException;
}
