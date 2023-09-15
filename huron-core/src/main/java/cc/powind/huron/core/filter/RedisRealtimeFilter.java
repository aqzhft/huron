package cc.powind.huron.core.filter;

import cc.powind.huron.core.exception.RealtimeExistException;
import cc.powind.huron.core.model.Realtime;
import cc.powind.huron.core.model.RealtimeFilter;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class RedisRealtimeFilter implements RealtimeFilter {

    /**
     * default expire timeout
     */
    private int timeout = 3 * 24 * 3600;

    /**
     * redis connection pool
     */
    private JedisPool pool;

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public JedisPool getPool() {
        return pool;
    }

    public void setPool(JedisPool pool) {
        this.pool = pool;
    }

    @Override
    public void exist(Realtime realtime) throws RealtimeExistException {
        try (Jedis jedis = pool.getResource()) {
            String set = jedis.set(realtime.getRealtimeId(), realtime.getRealtimeId(), "NX", "EX", timeout);
            if (set == null) {
                throw new RealtimeExistException(realtime);
            }
        }
    }
}
