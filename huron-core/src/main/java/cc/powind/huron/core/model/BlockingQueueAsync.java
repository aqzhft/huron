package cc.powind.huron.core.model;

import cc.powind.huron.core.utils.QueueUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public abstract class BlockingQueueAsync <T> implements Async <T>{

    protected Log log = LogFactory.getLog(getClass());

    /**
     * Real-time storage
     */
    private BlockingQueue<T> queue;

    /**
     * Maximum waiting time for pulling data
     */
    private long maxFetchWait = 3000L;

    /**
     * Minimum amount data pulled at once
     */
    private int minFetchSize = 1000;

    /**
     * maximum amount data pulled at once
     */
    private int maxFetchSize = 10000;

    /**
     * The capacity of Real-time storage
     */
    private int capacity = 2 << 16;

    public BlockingQueue<T> getQueue() {
        return queue;
    }

    public void setQueue(BlockingQueue<T> queue) {
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

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public BlockingQueueAsync() {

        queue = new ArrayBlockingQueue<>(capacity);

        initConsumeThread();
    }

    protected void initConsumeThread() {

        List<T> collect = new ArrayList<>();

        new Thread(() -> {
            while (true) {

                try {

                    collect.clear();

                    QueueUtils.drainTo(collect, queue, minFetchSize, maxFetchSize, maxFetchWait);

                    exec(collect);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, "blocking-queue-async-thread").start();
    }

    @Override
    public void submit(T t) {
        queue.offer(t);
    }

    @Override
    public void submit(Collection<T> list) {
        list.forEach(t -> queue.offer(t));
    }

    @Override
    public void exec(Collection<T> list) {};
}
