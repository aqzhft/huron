package cc.powind.huron.core.storage;

import cc.powind.huron.core.model.Realtime;

import java.util.Collections;
import java.util.List;

/**
 * 实时数据存储的默认实现
 */
public class DefaultStorage extends AbstractStorage {

    @Override
    protected void init() {

        this.setAsync(new Async() {

            @Override
            public void submit(Realtime realtime) {
                exec(Collections.singletonList(realtime));
            }

            @Override
            public void exec(List<Realtime> realtimeList) {
                doStore(realtimeList);
            }
        });
    }
}
