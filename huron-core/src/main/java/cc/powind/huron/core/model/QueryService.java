package cc.powind.huron.core.model;

import java.time.LocalDateTime;
import java.util.Collection;

public interface QueryService <T extends Realtime> {

    Collection<T> findUpToDate(String[] objectIds, LocalDateTime beginTime, LocalDateTime endTime);

    Collection<T> findAll(String[] objectIds, LocalDateTime beginTime, LocalDateTime endTime);
}
