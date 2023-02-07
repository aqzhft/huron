package cc.powind.huron.core.model;

import cc.powind.huron.core.model.Realtime;

import java.util.Collection;

public interface RealtimeMapper {

    /**
     * 是否支持此实时数据的处理
     *
     * @param realtime 实时数据
     * @return bool
     */
    boolean isSupport(Realtime realtime);

    /**
     * 单个的插入数据
     *
     * @param realtime 实时数据
     */
    void insert(Realtime realtime);

    /**
     * 批量的插入数据
     *
     * @param realtimeList 实时数据的集合
     */
    void insertBatch(Collection<Realtime> realtimeList);
}
