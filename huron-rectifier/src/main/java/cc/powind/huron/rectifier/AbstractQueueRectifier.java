package cc.powind.huron.rectifier;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

public abstract class AbstractQueueRectifier<T> extends DefaultRectifier <T> {

    protected final Log log = LogFactory.getLog(getClass());

    private long maxPushWait = 3000L;

    private long maxFetchWait = 3000L;

    private int minFetchSize = 1000;

    private int maxFetchSize = 10000;

    public long getMaxPushWait() {
        return maxPushWait;
    }

    public void setMaxPushWait(long maxPushWait) {
        this.maxPushWait = maxPushWait;
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

    @Override
    public void inflow(T t) {
        // put into queue
        offer(t, maxPushWait, TimeUnit.MILLISECONDS);
    }

    @Override
    public void inflow(Collection<T> list) {
        // put into queue batch
        list.forEach(t -> offer(t, maxFetchWait, TimeUnit.MILLISECONDS));
    }

    protected abstract void offer(T t, long timeout, TimeUnit unit);
}
