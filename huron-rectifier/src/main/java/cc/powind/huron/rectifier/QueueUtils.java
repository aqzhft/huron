package cc.powind.huron.rectifier;

import java.util.Collection;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class QueueUtils {

    /**
     * copy from guava
     */
    public static <T> int drainTo(Collection<T> buffer, BlockingQueue<T> queue, int numElements, int maxElements, long timeout) {

        long deadline = System.nanoTime() + TimeUnit.MILLISECONDS.toNanos(timeout);

        int added = 0;
        boolean interrupted = false;

        try {
            while (added < numElements) {

                // Attempt to retrieve the maximum quantity at once
                added += queue.drainTo(buffer, maxElements - added);

                // If the number of pulled out elements does not reach the minimum numElements, need to wait
                if (added < numElements) {

                    T realtime;
                    while (true) {
                        try {

                            // Attempt to pull data from queue for the remaining time
                            realtime = queue.poll(deadline - System.nanoTime(), TimeUnit.NANOSECONDS);

                            // The function of this infinite loop is to wait for new data, and if there is new data or the time expires, it will jump out of the loop
                            break;
                        } catch (InterruptedException e) {
                            interrupted = true;
                        }
                    }

                    if (realtime == null) {

                        // This indicates that if no data is retrieved at the expected time point, the loop will be stopped directly
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
