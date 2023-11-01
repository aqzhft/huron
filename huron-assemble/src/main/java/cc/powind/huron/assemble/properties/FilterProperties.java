package cc.powind.huron.assemble.properties;

public class FilterProperties {

    private RedisFilter redis = new RedisFilter();

    public RedisFilter getRedis() {
        return redis;
    }

    public void setRedis(RedisFilter redis) {
        this.redis = redis;
    }

    public static class RedisFilter {

        private String host = "localhost";

        private Integer port = 6379;

        /**
         * timeout (millisecond)
         */
        private Integer timeout = 1000;

        /**
         * realtime survival time (millisecond)
         */
        private Long ttl = 3 * 24 * 3600 * 1000L;

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public Integer getPort() {
            return port;
        }

        public void setPort(Integer port) {
            this.port = port;
        }

        public Integer getTimeout() {
            return timeout;
        }

        public void setTimeout(Integer timeout) {
            this.timeout = timeout;
        }

        public Long getTtl() {
            return ttl;
        }

        public void setTtl(Long ttl) {
            this.ttl = ttl;
        }
    }
}
