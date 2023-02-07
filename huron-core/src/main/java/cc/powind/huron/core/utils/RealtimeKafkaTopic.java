package cc.powind.huron.core.utils;

import cc.powind.huron.core.model.Realtime;
import cc.powind.huron.core.model.RealtimeRegister;

import java.util.Arrays;

public class RealtimeKafkaTopic {

    private static final String REALTIME_ROUTER_PREFIX = "realtime.router.";

    private static final String REALTIME_STORAGE_PREFIX = "realtime.storage.";

    private final RealtimeRegister register;

    public RealtimeKafkaTopic(RealtimeRegister register) {
        this.register = register;
    }

    public String getStorageTopic(Realtime realtime) {
        return REALTIME_STORAGE_PREFIX + register.getAlias(realtime.getClass());
    }

    public Class<? extends Realtime> getClazzFromTopic(String storageTopic) {
        return register.getClazz(storageTopic.replace(REALTIME_STORAGE_PREFIX, "").replace(REALTIME_ROUTER_PREFIX, ""));
    }

    public String[] getStorageTopic() {
        return Arrays.stream(register.getAlias()).map(alias -> REALTIME_STORAGE_PREFIX + alias).toArray(String[]::new);
    }

    public String[] getRouterTopic() {
        return Arrays.stream(register.getAlias()).map(alias -> REALTIME_ROUTER_PREFIX + alias).toArray(String[]::new);
    }
}
