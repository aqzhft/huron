package cc.powind.huron.rectifier;

import java.util.Collection;
import java.util.Collections;
import java.util.function.Consumer;

public class DefaultRectifier <T> implements Rectifier <T> {

    private String name = Integer.toHexString(hashCode());

    private Consumer<Collection<T>> callback;

    public DefaultRectifier() {
    }

    public void init() {}

    public DefaultRectifier(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void inflow(T t) {
        inflow(Collections.singletonList(t));
    }

    @Override
    public void inflow(Collection<T> list) {
        callback(list);
    }

    @Override
    public void outflow(Consumer<Collection<T>> consumer) {
        this.callback = consumer;
    }

    protected Consumer<Collection<T>> getCallback() {
        return callback;
    }

    protected void callback(Collection<T> list) {

        if (callback == null || list == null || list.isEmpty()) {
            return;
        }

        callback.accept(list);
    }
}
