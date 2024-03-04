package cc.powind.huron.core.filter;

import cc.powind.huron.core.exception.RealtimeExistException;
import cc.powind.huron.core.model.Realtime;
import cc.powind.huron.core.model.RealtimeFilter;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class OffsetRealtimeFilter implements RealtimeFilter {

    private static final Map<String, LockInfo> offset = new HashMap<>();

    @Override
    public void exist(Realtime realtime) throws RealtimeExistException {

        LockInfo lockInfo = offset.computeIfAbsent(realtime.getObjectId(), key -> new LockInfo());
        lockInfo.getLock().lock();
        try {
            if (lockInfo.getValue() >= realtime.getTime().toEpochMilli()) {
                throw new RealtimeExistException(realtime);
            }
            lockInfo.setValue(realtime.getTime().toEpochMilli());
        } finally {
            lockInfo.getLock().unlock();
        }
    }

    static class LockInfo {

        private Lock lock = new ReentrantLock();

        private Long value = -1L;

        public Lock getLock() {
            return lock;
        }

        public void setLock(Lock lock) {
            this.lock = lock;
        }

        public Long getValue() {
            return value;
        }

        public void setValue(Long value) {
            this.value = value;
        }
    }
}
