package cc.powind.huron.core.model;

import java.time.LocalDateTime;
import java.util.Collection;

/**
 * 实时数据 查询器
 */
public interface QueryService {

    /**
     * 查询指定对象在指定时间范围内最新的数据
     *
     * @param objectIds 实时数据唯一标识集合
     * @param beginTime 开始时间
     * @param endTime 结束时间
     * @return 实时数据的集合
     */
    Collection<? extends Realtime> findUpToDate(String[] objectIds, LocalDateTime beginTime, LocalDateTime endTime);

    /**
     * 查询指定对象在指定时间范围内的所有数据
     *
     * @param objectIds 实时数据唯一标识集合
     * @param beginTime 开始时间
     * @param endTime 结束时间
     * @return 实时数据的集合
     */
    Collection<? extends Realtime> findAll(String[] objectIds, LocalDateTime beginTime, LocalDateTime endTime);
}
