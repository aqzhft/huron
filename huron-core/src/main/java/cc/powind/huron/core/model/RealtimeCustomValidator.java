package cc.powind.huron.core.model;

/**
 *
 * 自定义校验器
 *
 */
public interface RealtimeCustomValidator {

    /**
     * 校验
     *
     * @param realtime realtime
     */
    void validate(Realtime realtime);

    /**
     * 检查是否支持此实时数据的校验
     *
     * @param realtime realtime
     * @return bool
     */
    boolean isSupport(Realtime realtime);
}
