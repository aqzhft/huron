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
     * 实时数据的存储容器
     */
    private BlockingQueue<T> queue;

    /**
     * 拉取数据最大等待时间
     */
    private long maxFetchWait = 3000L;

    /**
     * 拉取数据最少拉取的数量
     */
    private int minFetchSize = 1000;

    /**
     * 拉取数据最多拉取的数量
     */
    private int maxFetchSize = 10000;

    /**
     * 容量
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

        // 初始化queue
        queue = new ArrayBlockingQueue<>(capacity);

        // 消费数据
        initConsumeThread();
    }

    /**
     * 需要起一个消费进程实时拉取queue中的数据做后步处理
     */
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
