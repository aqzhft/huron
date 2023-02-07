package cc.powind.huron.core.model;

public interface RealtimeRegister {

    /**
     * 注册一个实时数据
     * 别名和类是一对一的关系
     *
     * @param alias 别名
     * @param clazz 类
     */
    void register(String alias, Class<? extends Realtime> clazz);

    /**
     * 根据类获取别名
     *
     * @param clazz 类
     * @return 别名
     */
    String getAlias(Class<? extends Realtime> clazz);

    /**
     * 根据别名获取类
     *
     * @param alias 别名
     * @return 类
     */
    Class<? extends Realtime> getClazz(String alias);

    /**
     * 查询所有的别名
     *
     * @return 别名
     */
    String[] getAlias();
}
