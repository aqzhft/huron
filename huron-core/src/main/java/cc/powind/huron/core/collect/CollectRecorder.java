package cc.powind.huron.core.collect;

import cc.powind.huron.core.exception.RealtimeExistException;
import cc.powind.huron.core.exception.RealtimeValidateException;
import cc.powind.huron.core.model.Realtime;

import java.time.Instant;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

public class CollectRecorder {

    private int CAPACITY = 100;

    private final RealtimeValidateException[] invalidExceptionArray = new RealtimeValidateException[CAPACITY];

    private final RealtimeExistException[] existExceptionArray = new RealtimeExistException[CAPACITY];

    private final Exception[] otherExceptionArray = new Exception[CAPACITY];

    private static final AtomicLong invalidException = new AtomicLong(0);

    private static final AtomicLong existException = new AtomicLong(0);

    private static final AtomicLong otherException = new AtomicLong(0);

    private static final AtomicLong total = new AtomicLong(0);

    private static final AtomicLong metrics = new AtomicLong(0);

    public CollectRecorder() {
    }

    public CollectRecorder(int CAPACITY) {
        this.CAPACITY = CAPACITY;
    }

    public void success() {
        total.incrementAndGet();
    }

    public void metrics(int size) {
        metrics.getAndAdd(size);
    }

    public synchronized void isError(Exception e) {
        if (e.getClass().equals(RealtimeValidateException.class)) {
            long count = invalidException.incrementAndGet();
            invalidExceptionArray[(int) (count % CAPACITY)] = (RealtimeValidateException) e;
        } else if (e.getClass().equals(RealtimeExistException.class)) {
            long count = existException.incrementAndGet();
            existException.incrementAndGet();
            existExceptionArray[(int) (count % CAPACITY)] = (RealtimeExistException) e;
        } else {
            long count = otherException.incrementAndGet();
            otherException.incrementAndGet();
            otherExceptionArray[(int) (count % CAPACITY)] = e;
        }
    }

    public RealtimeValidateException[] getInvalidList() {
        return Arrays.stream(invalidExceptionArray).filter(Objects::nonNull).toArray(RealtimeValidateException[]::new);
    }

    public RealtimeExistException[] getExistList() {
        return Arrays.stream(existExceptionArray).filter(Objects::nonNull).toArray(RealtimeExistException[]::new);
    }

    public Exception[] getOtherList() {
        return Arrays.stream(otherExceptionArray).filter(Objects::nonNull).toArray(Exception[]::new);
    }

    public Long getInvalidCount() {
        return invalidException.get();
    }

    public Long getExistCount() {
        return existException.get();
    }

    public Long getOtherCount() {
        return otherException.get();
    }

    public Long getTotal() {
        return total.get();
    }

    public Long getMetricCount() {
        return metrics.get();
    }

    public Realtime realtime() {
        CollectRecordRealtime realtime = new CollectRecordRealtime();
        realtime.setTotal(total.get());
        realtime.setInvalid(invalidException.get());
        realtime.setExist(existException.get());
        realtime.setOther(otherException.get());
        realtime.setMetrics(metrics.get());
        realtime.setTime(Instant.now());
        return realtime;
    }
}