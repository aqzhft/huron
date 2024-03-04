package cc.powind.huron.core.collect;

import cc.powind.huron.core.exception.RealtimeExistException;
import cc.powind.huron.core.exception.RealtimeStoreException;
import cc.powind.huron.core.exception.RealtimeValidateException;
import cc.powind.huron.core.model.*;
import cc.powind.huron.core.storage.RealtimeStorage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class CollectServiceImpl implements CollectService {

    protected final Log log = LogFactory.getLog(getClass());

    private CollectRecorder collectRecorder = new CollectRecorder();

    private List<RealtimeFilter> filters;

    private List<RealtimeValidator> validators;

    private List<MetricDetector> detectors;

    private List<MetricHandler> metricHandlers;

    private RealtimeStorage storage;

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

    public RealtimeStorage getStorage() {
        return storage;
    }

    public void setStorage(RealtimeStorage storage) {
        this.storage = storage;
    }

    public void init() {
        initRecorderCalc();
    }

    @Override
    public void collect(Realtime realtime) throws RealtimeException {

        RealtimeError realtimeError = validate(realtime);
        if (!realtimeError.errorIsEmpty()) {
            throw new RealtimeValidateException(realtimeError.getErrorDescription().toArray(new String[0]));
        }

        determineIfExisted(realtime);

        store(realtime);

        compute(realtime);
    }

    @Override
    public <T extends Realtime> void collect(RealtimeWrapper<T> wrapper) throws RealtimeException {

        List<T> realtimeList = wrapper.getRealtimeList();

        List<RealtimeError> errorList = realtimeList.stream().map(this::validate)
                .filter(error -> !error.errorIsEmpty())
                .collect(Collectors.toList());

        if (!errorList.isEmpty()) {
            String[] errorTexts = errorList.stream().map(RealtimeError::getErrorDescription)
                    .flatMap(Collection::stream)
                    .toArray(String[]::new);
            throw new RealtimeValidateException(errorTexts);
        }

        List<T> remainedList = notExisted(realtimeList);

        remainedList.sort(Comparator.comparing(Realtime::getTime));

        store(remainedList);

        compute(remainedList);
    }

    protected RealtimeError validate(Realtime realtime) {

        if (validators == null || validators.isEmpty()) {
            return null;
        }

        List<RealtimeError.Error> errorList = validators.stream().filter(validator -> validator.isSupport(realtime))
                .map(validator -> validator.validate(realtime).getErrors())
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        return new RealtimeError(realtime, errorList);
    }

    protected void determineIfExisted(Realtime realtime) throws RealtimeExistException {

        if (filters == null || filters.isEmpty()) {
            return;
        }

        for (RealtimeFilter filter : filters) {
            filter.exist(realtime);
        }
    }

    protected <T extends Realtime> List<T> notExisted(List<T> realtimeList) {

        if (filters == null || filters.isEmpty()) {
            return realtimeList;
        }

        return realtimeList.stream().filter(realtime -> {
            for (RealtimeFilter filter : filters) {
                try {
                    filter.exist(realtime);
                } catch (RealtimeExistException e) {
                    return false;
                }
            }
            return true;
        }).collect(Collectors.toList());
    }

    protected void compute(Realtime realtime) {

        if (detectors == null || detectors.isEmpty()) {
            return;
        }

        // Detect metrics from realtime
        Collection<Metric> metrics = detect(realtime);

        // count
        collectRecorder.metrics(metrics.size());

        // Custom processing logic
        metricHandle(metrics);
    }

    protected <T extends Realtime> void compute(List<T> realtimeList) {

        if (detectors == null || detectors.isEmpty()) {
            return;
        }

        List<Metric> metrics = realtimeList.stream().map(this::detect)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

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

    protected void store(Realtime realtime) throws RealtimeStoreException {
        storage.store(realtime);
    }

    protected <T extends Realtime> void store(List<T> realtimeList) throws RealtimeStoreException {
        storage.store(realtimeList);
    }

    protected void initRecorderCalc() {
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            compute(collectRecorder.realtime());
        }, 10, 1, TimeUnit.SECONDS);
    }
}
