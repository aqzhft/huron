package cc.powind.huron.rectifier;

import java.util.Collection;
import java.util.function.Consumer;

public interface Rectifier <T> {

    String getName();

    void inflow(T t);

    void inflow(Collection<T> list);

    void outflow(Consumer<Collection<T>> consumer);
}
