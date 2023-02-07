package cc.powind.huron.core.collect;

import cc.powind.huron.core.model.Realtime;

/**
 *
 * 数据收集服务
 *
 */
public interface CollectService {

    /**
     * 数据收集
     *
     * 1、对数据的基本校验
     *
     * 2、数据的筛选过滤
     *
     * 3、数据的实时计算
     *
     * 4、数据的持久化
     *
     * @param realtime realtime
     */
    void collect(Realtime realtime);
}
