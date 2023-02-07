package cc.powind.huron.core.model;

import java.time.Instant;
import java.util.Map;

public abstract class AbstractAbnormal implements Abnormal {

    private String message;

    private Instant time;

    public AbstractAbnormal() {
    }

    public AbstractAbnormal(String message) {
        this.message = message;
        this.time = Instant.now();
    }

    public AbstractAbnormal(String message, Instant time) {
        this.message = message;
        this.time = time;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public Instant getTime() {
        return time;
    }

    public void setTime(Instant time) {
        this.time = time;
    }

    @Override
    public Map<String, String> validate() {
        return null;
    }
}
