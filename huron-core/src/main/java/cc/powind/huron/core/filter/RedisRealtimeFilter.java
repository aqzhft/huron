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
    private long ttl = 3 * 24 * 3600;

    /**
     * redis connection pool
     */
    private JedisPool pool;

    public long getTtl() {
        return ttl;
    }

    public void setTtl(long ttl) {
        this.ttl = ttl;
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
            String set = jedis.set(realtime.getRealtimeId(), realtime.getRealtimeId(), "NX", "EX", ttl);
            if (set == null) {
                throw new RealtimeExistException(realtime);
            }
        }
    }
}
