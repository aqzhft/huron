package cc.powind.huron.core.model;

public interface RealtimeCustomValidator {

    void validate(Realtime realtime);

    boolean isSupport(Realtime realtime);
}
