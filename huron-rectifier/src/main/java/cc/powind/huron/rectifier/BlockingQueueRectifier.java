package cc.powind.huron.rectifier;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class BlockingQueueRectifier <T> extends AbstractQueueRectifier <T> implements Runnable {

    private BlockingQueue<T> queue = new ArrayBlockingQueue<>(2 << 16);

    private final List<T> collect = new ArrayList<>();

    public BlockingQueueRectifier() {
        new Thread(this, "blocking_queue_rectifier_runnable").start();
    }

    public BlockingQueue<T> getQueue() {
        return queue;
    }

    public void setQueue(BlockingQueue<T> queue) {
        this.queue = queue;
    }

    @Override
    public void init() {

    }

    @Override
    protected void offer(T t, long timeout, TimeUnit unit) {
        try {
            queue.offer(t, timeout, unit);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {

        //noinspection InfiniteLoopStatement
        while (true) {
            try {

                collect.clear();

                int length = QueueUtils.drainTo(collect, this.queue, getMinFetchSize(), getMaxFetchSize(), getMaxFetchWait());

                log.info(" ================>  fetch data size: " + length + "  <================== ");

                callback(collect);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
