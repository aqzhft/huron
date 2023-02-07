package cc.powind.huron.core.utils;

import cc.powind.huron.core.model.Realtime;
import cc.powind.huron.core.model.RealtimeRegister;

import java.util.Arrays;

public class RealtimeRabbitmqQueue {

    private static final String EXCHANGE_NAME = "realtime.storage", EXCHANGE_TYPE = "direct";

    private static final String STORAGE_PREFIX = "realtime.storage.";

    private final RealtimeRegister register;

    public RealtimeRabbitmqQueue(RealtimeRegister register) {
        this.register = register;
    }

    public String getExchangeType() {
        return EXCHANGE_TYPE;
    }

    public String getStorageExchangeName() {
        return EXCHANGE_NAME;
    }

    public String[] storageQueue() {
        return Arrays.stream(register.getAlias()).map(alias -> STORAGE_PREFIX + alias).toArray(String[]::new);
    }

    public String storageQueue(Realtime realtime) {
        return STORAGE_PREFIX + register.getAlias(realtime.getClass());
    }

    public Class<? extends Realtime> getClazzFromRouteKey(String routeKey) {
        return register.getClazz(routeKey.replace(STORAGE_PREFIX, ""));
    }
}
