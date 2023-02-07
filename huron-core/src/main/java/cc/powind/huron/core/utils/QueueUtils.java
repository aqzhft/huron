package cc.powind.huron.core.utils;

import cc.powind.huron.core.model.Realtime;

import java.util.Collection;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class QueueUtils {

    /**
     * 批量拉取数据（抄的guava）
     *
     *
     * @param queue       队列
     * @param numElements 拉取的最少的量
     * @param maxElements 拉取的最多的量
     * @param timeout     超时时间（毫秒）
     * @return 拉取出的数量
     */
    public static <T> int drainTo(Collection<T> buffer, BlockingQueue<T> queue, int numElements, int maxElements, long timeout) {

        long deadline = System.nanoTime() + TimeUnit.MILLISECONDS.toNanos(timeout);

        int added = 0;
        boolean interrupted = false;

        try {
            while (added < numElements) {

                // 尝试一次性取出最大的数量
                added += queue.drainTo(buffer, maxElements - added);

                // 如果拉出的数量还达不到最少的量numElements，则需要等待
                if (added < numElements) {

                    T realtime;
                    while (true) {
                        try {

                            // 在剩余的时间里尝试从queue里拉数据
                            realtime = queue.poll(deadline - System.nanoTime(), TimeUnit.NANOSECONDS);

                            // 这个无限循环的作用就是等待新的数据，如果有新数据或者时间到期，就跳出循环
                            break;
                        } catch (InterruptedException e) {
                            interrupted = true;
                        }
                    }

                    if (realtime == null) {

                        // 这里说明到了预期的时间点依然没有取到数据，则直接将循环停掉
                        break;
                    }

                    buffer.add(realtime);
                    added++;
                }
            }
        } finally {
            if (interrupted) {
                Thread.currentThread().interrupt();
            }
        }

        return added;
    }
}
