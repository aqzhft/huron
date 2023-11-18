package cc.powind.huron.core.model;

import java.util.HashMap;
import java.util.Map;

public class DefaultRealtimeRegister implements RealtimeRegister {

    private final Map<String, Class<? extends Realtime>> map = new HashMap<>();

    public DefaultRealtimeRegister() {
        init();
    }

    @Override
    public void register(String alias, Class<? extends Realtime> clazz) {
        map.put(alias, clazz);
    }

    @Override
    public String getAlias(Class<? extends Realtime> clazz) {
        for (String alias : map.keySet()) {
            if (clazz.equals(map.get(alias))) {
                return alias;
            }
        }
        return null;
    }

    @Override
    public Class<? extends Realtime> getClazz(String alias) {
        return map.get(alias);
    }

    @Override
    public String[] getAlias() {
        return map.keySet().toArray(new String[0]);
    }

    @Override
    public Map<String, Class<? extends Realtime>> getMappings() {
        return map;
    }

    protected void init() {}
}
