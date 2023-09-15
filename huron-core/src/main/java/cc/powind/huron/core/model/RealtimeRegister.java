package cc.powind.huron.core.model;

public interface RealtimeRegister {

    /**
     * Register a real-time type
     * alias and clazz is one-for-one
     *
     * @param alias alias
     * @param clazz clazz
     */
    void register(String alias, Class<? extends Realtime> clazz);

    /**
     * get alias name by clazz
     *
     * @param clazz clazz
     * @return alias
     */
    String getAlias(Class<? extends Realtime> clazz);

    /**
     * get clazz by alias name
     *
     * @param alias alias
     * @return clazz
     */
    Class<? extends Realtime> getClazz(String alias);

    /**
     * get all alias names
     *
     * @return alias
     */
    String[] getAlias();
}
