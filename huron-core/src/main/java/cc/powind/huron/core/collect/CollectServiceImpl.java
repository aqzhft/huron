package cc.powind.huron.core.collect;

import cc.powind.huron.core.exception.RealtimeExistException;
import cc.powind.huron.core.exception.RealtimeValidateException;
import cc.powind.huron.core.model.*;
import cc.powind.huron.core.storage.RealtimeStorage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class CollectServiceImpl implements CollectService {

    protected final Log log = LogFactory.getLog(getClass());

    private final RealtimeStat realtimeStat = new RealtimeStat();

    private List<RealtimeFilter> filters;

    private List<RealtimeCustomValidator> customValidators;

    private List<MetricDetector> detectors;

    private List<MetricHandler> metricHandlers;

    private List<RealtimeStorage> storages;

    public List<RealtimeFilter> getFilters() {
        return filters;
    }

    public void setFilters(List<RealtimeFilter> filters) {
        this.filters = filters;
    }

    public List<RealtimeCustomValidator> getCustomValidators() {
        return customValidators;
    }

    public void setCustomValidators(List<RealtimeCustomValidator> customValidators) {
        this.customValidators = customValidators;
    }

    public List<MetricDetector> getDetectors() {
        return detectors;
    }

    public void setDetectors(List<MetricDetector> detectors) {
        this.detectors = detectors;
    }

    public List<MetricHandler> getMetricHandlers() {
        return metricHandlers;
    }

    public void setMetricHandlers(List<MetricHandler> metricHandlers) {
        this.metricHandlers = metricHandlers;
    }

    public List<RealtimeStorage> getStorages() {
        return storages;
    }

    public void setStorages(List<RealtimeStorage> storages) {
        this.storages = storages;
    }

    @Override
    public void collect(Realtime realtime) {

        try {

            validate(realtime);

            filter(realtime);

            customValidate(realtime);

            compute(realtime);

            store(realtime);

        } catch (RealtimeValidateException ve) {

            // 错误处理
            long invalidCount = realtimeStat.invalidCount.incrementAndGet();

            if (invalidCount % 2 == 0) {
                log.error("invalid realtime: " + String.join(",", ve.getErrTexts()) + ", entity: " + ve.getRealtime());
            }


        } catch (RealtimeExistException ee) {

            //
            realtimeStat.existCount.incrementAndGet();


        } catch (Exception e) {

            // xx
            e.printStackTrace();


        } finally {
            long total = realtimeStat.total.incrementAndGet();

            if (total % 100 == 0) {
                log.info("summary [ total: " + total + ", invalid: " + realtimeStat.invalidCount + ", exist: " + realtimeStat.existCount + " ]");
            }
        }
    }

    protected void validate(Realtime realtime) throws RealtimeException {

        if (realtime == null) {
            throw new RealtimeValidateException(null, "realtime is null");
        }

        Map<String, String> errMap = realtime.validate();
        if (errMap != null && !errMap.isEmpty()) {
            throw new RealtimeValidateException(realtime, errMap.values().toArray(new String[0]));
        }
    }

    protected void filter(Realtime realtime) throws RealtimeException {

        if (filters == null || filters.isEmpty()) {
            return;
        }

        for (RealtimeFilter filter : filters) {
            filter.exist(realtime);
        }
    }

    protected void customValidate(Realtime realtime) {

        if (customValidators == null || customValidators.isEmpty()) {
            return;
        }

        for (RealtimeCustomValidator validator : customValidators) {
            if (validator.isSupport(realtime)) {
                validator.validate(realtime);
            }
        }
    }

    protected void compute(Realtime realtime) {

        if (detectors == null || detectors.isEmpty()) {
            return;
        }

        // detect metrics from realtime
        Collection<Metric> metrics = detect(realtime);

        // custom processing logic
        metricHandle(metrics);
    }

    protected Collection<Metric> detect(Realtime realtime) {
        return detectors.stream().filter(detector -> detector.isSupport(realtime))
                .map(detector -> detector.detect(realtime))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    protected void metricHandle(Collection<Metric> metrics) {

        if (metricHandlers == null || metricHandlers.isEmpty()) {
            return;
        }

        for (MetricHandler handler : metricHandlers) {
            for (Metric metric : metrics) {
                if (handler.isSupport(metric)) {
                    handler.handle(metric);
                }
            }
        }
    }

    public void store(Realtime realtime) {

        if (storages == null || storages.isEmpty()) {
            return;
        }

        storages.forEach(storage -> storage.store(realtime));
    }

    public static class RealtimeStat {

        private final AtomicLong total = new AtomicLong(0);

        private final AtomicLong invalidCount = new AtomicLong(0);

        private final AtomicLong existCount = new AtomicLong(0);

        public AtomicLong getTotal() {
            return total;
        }

        public AtomicLong getInvalidCount() {
            return invalidCount;
        }

        public AtomicLong getExistCount() {
            return existCount;
        }
    }
}
