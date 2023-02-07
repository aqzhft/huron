package cc.powind.huron.core.model;

import java.util.Collection;

public interface Async <T> {

    void submit(T t);

    void submit(Collection<T> list);

    void exec(Collection<T> list);
}
