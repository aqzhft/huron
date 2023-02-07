package cc.powind.huron.core.collect;

import cc.powind.huron.core.exception.RealtimeExistException;
import cc.powind.huron.core.exception.RealtimeValidateException;
import cc.powind.huron.core.model.RealtimeMapper;
import cc.powind.huron.core.model.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * 数据收集 默认实现
 *
 * 1、基本的数据校验
 * 数据的基本校验由detector进行，但是为了安全性，还是需要再加一层校验
 *
 * 2、数据过滤
 * 需要过滤掉重复数据，并实时计算重复率
 * 接口实现{@link RealtimeFilter}
 *
 * 3、自定义校验
 * 增加一个数据自定义校验的扩展点
 *
 * 4、实时计算
 * 通过实时计算，将实时数据中的metric{@link Metric}提取出来
 * 具体实现接口{@link MetricDetector}
 *
 * 5、指标处理
 * - 第一个可以用于实时判断，例如判断是否超过系统设置的阈值
 * - 指标是否需要持久化
 *
 * 6、实时数据持久化
 * 持久化接口{@link RealtimeMapper}
 *
 * 数据重复率的统计
 *
 *
 */
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

            Collection<Metric> metrics = compute(realtime);

            metricHandle(metrics);

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

    /**
     * 对实时数据基本格式的校验
     *
     * @param realtime 实时数据
     * @throws RealtimeValidateException 实时数据校验错误
     */
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

    /**
     * 实时数据的自定义校验
     *
     * @param realtime 实时数据
     */
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

    /**
     * 实时计算
     *
     * @param realtime 实时数据
     * @return metric集合
     */
    protected Collection<Metric> compute(Realtime realtime) {

        if (detectors == null || detectors.isEmpty()) {
            return Collections.emptyList();
        }

        return detectors.stream().filter(detector -> detector.isSupport(realtime))
                .map(detector -> detector.detect(realtime))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    /**
     * 指标处理
     *
     * @param metrics 指标集合
     */
    public void metricHandle(Collection<Metric> metrics) {

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

    /**
     * 实时数据的存储
     *
     * @param realtime 实时数据
     */
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
