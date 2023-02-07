package cc.powind.huron.assemble.properties;

public class StorageProperties {

    /**
     * 拉取数据最大等待时间
     */
    private Long maxFetchWait = 3000L;

    /**
     * 一次性拉取最少的量
     */
    private Integer minFetchSize = 1000;

    /**
     * 一次性拉取最大的量
     */
    private Integer maxFetchSize = 10000;

    /**
     * kafka的配置
     */
    private Kafka kafka = new Kafka();

    /**
     * rabbitmq的配置
     */
    private Rabbitmq rabbitmq = new Rabbitmq();

    public Long getMaxFetchWait() {
        return maxFetchWait;
    }

    public void setMaxFetchWait(Long maxFetchWait) {
        this.maxFetchWait = maxFetchWait;
    }

    public Integer getMinFetchSize() {
        return minFetchSize;
    }

    public void setMinFetchSize(Integer minFetchSize) {
        this.minFetchSize = minFetchSize;
    }

    public Integer getMaxFetchSize() {
        return maxFetchSize;
    }

    public void setMaxFetchSize(Integer maxFetchSize) {
        this.maxFetchSize = maxFetchSize;
    }

    public Kafka getKafka() {
        return kafka;
    }

    public void setKafka(Kafka kafka) {
        this.kafka = kafka;
    }

    public Rabbitmq getRabbitmq() {
        return rabbitmq;
    }

    public void setRabbitmq(Rabbitmq rabbitmq) {
        this.rabbitmq = rabbitmq;
    }

    public static class Kafka {

        /**
         * kafka服务地址
         */
        private String servers;

        /**
         * 回执
         */
        private String acks = "0";

        /**
         * 消费组ID
         */
        private String groupId = "storage.groupId";

        /**
         * 消费点
         */
        private String autoOffsetReset = "earliest";

        /**
         * 一次性拉取最少的量（默认1M）
         */
        private int fetchMinSize = 1048576;

        /**
         * 一次性拉取最大的量（默认10M）
         */
        private int fetchMaxSize = 10485760;

        public String getServers() {
            return servers;
        }

        public void setServers(String servers) {
            this.servers = servers;
        }

        public String getAcks() {
            return acks;
        }

        public void setAcks(String acks) {
            this.acks = acks;
        }

        public String getGroupId() {
            return groupId;
        }

        public void setGroupId(String groupId) {
            this.groupId = groupId;
        }

        public String getAutoOffsetReset() {
            return autoOffsetReset;
        }

        public void setAutoOffsetReset(String autoOffsetReset) {
            this.autoOffsetReset = autoOffsetReset;
        }

        public int getFetchMinSize() {
            return fetchMinSize;
        }

        public void setFetchMinSize(int fetchMinSize) {
            this.fetchMinSize = fetchMinSize;
        }

        public int getFetchMaxSize() {
            return fetchMaxSize;
        }

        public void setFetchMaxSize(int fetchMaxSize) {
            this.fetchMaxSize = fetchMaxSize;
        }
    }

    public static class Rabbitmq {

        private String host;

        private int port = 5672;

        private String userName;

        private String password;

        private String virtualHost = "/";

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getVirtualHost() {
            return virtualHost;
        }

        public void setVirtualHost(String virtualHost) {
            this.virtualHost = virtualHost;
        }
    }
}
