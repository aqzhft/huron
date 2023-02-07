package cc.powind.huron.assemble.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "huron")
public class HuronProperties {

    private Filter filter = new Filter();

    private RouterProperties router = new RouterProperties();

    private StorageProperties storage = new StorageProperties();

    public Filter getFilter() {
        return filter;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }

    public RouterProperties getRouter() {
        return router;
    }

    public void setRouter(RouterProperties router) {
        this.router = router;
    }

    public StorageProperties getStorage() {
        return storage;
    }

    public void setStorage(StorageProperties storage) {
        this.storage = storage;
    }

    public static class Filter {

        /**
         * 超时时间（毫秒）
         */
        private Long timeout = 3 * 24 * 3600 * 1000L;

        public Long getTimeout() {
            return timeout;
        }

        public void setTimeout(Long timeout) {
            this.timeout = timeout;
        }
    }
}
