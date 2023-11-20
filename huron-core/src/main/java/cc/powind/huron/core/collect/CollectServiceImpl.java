package cc.powind.huron.core.collect;

import cc.powind.huron.core.exception.RealtimeValidateException;
import cc.powind.huron.core.model.*;
import cc.powind.huron.core.storage.RealtimeStorage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class CollectServiceImpl implements CollectService {

    protected final Log log = LogFactory.getLog(getClass());

    private CollectRecorder collectRecorder = new CollectRecorder();

    private List<RealtimeFilter> filters;

    private List<RealtimeValidator> validators;

    private List<MetricDetector> detectors;

    private List<MetricHandler> metricHandlers;

    private List<RealtimeStorage> storages;

    private final ScheduledExecutorService scheduledExecutorService = new ScheduledThreadPoolExecutor(1);

    public CollectRecorder getCollectRecorder() {
        return collectRecorder;
    }

    public void setCollectRecorder(CollectRecorder collectRecorder) {
        this.collectRecorder = collectRecorder;
    }

    public List<RealtimeFilter> getFilters() {
        return filters;
    }

    public void setFilters(List<RealtimeFilter> filters) {
        this.filters = filters;
    }

    public List<RealtimeValidator> getValidators() {
        return validators;
    }

    public void setValidators(List<RealtimeValidator> validators) {
        this.validators = validators;
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

    public void init() {
        initRecorderCalc();
    }

    @Override
    public void collect(Realtime realtime) {

        try {

            validate(realtime);

            filter(realtime);

            compute(realtime);

            store(realtime);

        } catch (RealtimeException re) {
            collectRecorder.isError(re);
        } finally {
            collectRecorder.success();
        }
    }

    protected void validate(Realtime realtime) throws RealtimeValidateException {

        if (validators == null || validators.isEmpty()) {
            return;
        }

        for (RealtimeValidator validator : validators) {
            if (validator.isSupport(realtime)) {
                validator.validate(realtime);
            }
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

    protected void compute(Realtime realtime) {

        if (detectors == null || detectors.isEmpty()) {
            return;
        }

        // detect metrics from realtime
        Collection<Metric> metrics = detect(realtime);

        // count
        collectRecorder.metrics(metrics.size());

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

    protected void initRecorderCalc() {
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            compute(collectRecorder.realtime());
        }, 10, 1, TimeUnit.SECONDS);
    }
}
