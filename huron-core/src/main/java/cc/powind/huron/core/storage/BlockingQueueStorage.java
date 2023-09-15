package cc.powind.huron.core.storage;

import cc.powind.huron.core.model.Realtime;
import cc.powind.huron.core.utils.QueueUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

public class BlockingQueueStorage extends AbstractStorage {

    private BlockingQueue<Realtime> queue = new ArrayBlockingQueue<>(2 << 16);

    private long maxFetchWait = 3000L;

    private int minFetchSize = 1000;

    private int maxFetchSize = 10000;

    private ExecutorService executorService;

    public BlockingQueue<Realtime> getQueue() {
        return queue;
    }

    public void setQueue(BlockingQueue<Realtime> queue) {
        this.queue = queue;
    }

    public long getMaxFetchWait() {
        return maxFetchWait;
    }

    public void setMaxFetchWait(long maxFetchWait) {
        this.maxFetchWait = maxFetchWait;
    }

    public int getMinFetchSize() {
        return minFetchSize;
    }

    public void setMinFetchSize(int minFetchSize) {
        this.minFetchSize = minFetchSize;
    }

    public int getMaxFetchSize() {
        return maxFetchSize;
    }

    public void setMaxFetchSize(int maxFetchSize) {
        this.maxFetchSize = maxFetchSize;
    }

    public ExecutorService getExecutorService() {
        return executorService;
    }

    public void setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
    }

    @Override
    protected void init() {

        this.initAsync();

        this.initConsumeThread();
    }

    protected void initConsumeThread() {

        List<Realtime> collect = new ArrayList<>();

        executorService.submit(() -> {

            //noinspection InfiniteLoopStatement
            while (true) {

                try {

                    collect.clear();

                    int length = QueueUtils.drainTo(collect, this.queue, minFetchSize, maxFetchSize, maxFetchWait);

                    log.info(" ================>  fetch data size: " + length + "  <================== ");

                    this.getAsync().exec(collect);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 定制async方法
     */
    protected void initAsync() {

        this.setAsync(new Async() {

            @Override
            public void submit(Realtime realtime) {
                try {
                    queue.offer(realtime, 3000, TimeUnit.MILLISECONDS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void exec(List<Realtime> realtimeList) {
                doStore(realtimeList);
            }
        });
    }
}
