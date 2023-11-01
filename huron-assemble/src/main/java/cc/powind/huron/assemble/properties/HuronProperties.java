package cc.powind.huron.assemble.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "huron")
public class HuronProperties {

    private FilterProperties filter = new FilterProperties();

    private RouterProperties router = new RouterProperties();

    private StorageProperties storage = new StorageProperties();

    public FilterProperties getFilter() {
        return filter;
    }

    public void setFilter(FilterProperties filter) {
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
}
