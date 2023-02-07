package cc.powind.huron.core.model;

/**
 * 异常信息
 */
public interface Abnormal extends Realtime {

    /**
     * 描述
     *
     * @return 具体的描述信息
     */
    String getMessage();
}
