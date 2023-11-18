package cc.powind.huron.core.collect;

import cc.powind.huron.core.model.Abnormal;

public interface AbnormalHandler {

    void handle(Abnormal abnormal);

    boolean isSupport(Abnormal abnormal);
}
