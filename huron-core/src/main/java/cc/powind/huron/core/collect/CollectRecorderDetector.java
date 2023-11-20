package cc.powind.huron.core.collect;

import cc.powind.huron.core.model.BaseMetric;
import cc.powind.huron.core.model.Metric;
import cc.powind.huron.core.model.MetricDetector;
import cc.powind.huron.core.model.Realtime;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CollectRecorderDetector implements MetricDetector {

    @Override
    public Collection<? extends Metric> detect(Realtime realtime) {

        CollectRecordRealtime bean = (CollectRecordRealtime) realtime;
        List<BaseMetric> metricList = new ArrayList<>();

        if (bean.getTotal() > 0) {
            BaseMetric metric = new BaseMetric();
            metric.setMetricId(bean.getObjectId() + "_total");
            metric.setValue((double) bean.getTotal());
            metric.setTime(bean.getTime());
            metric.setSource(bean);
            metricList.add(metric);
        }

        if (bean.getInvalid() > 0) {
            BaseMetric metric = new BaseMetric();
            metric.setMetricId(bean.getObjectId() + "_invalid");
            metric.setValue((double) bean.getInvalid());
            metric.setTime(bean.getTime());
            metric.setSource(bean);
            metricList.add(metric);
        }

        if (bean.getExist() > 0) {
            BaseMetric metric = new BaseMetric();
            metric.setMetricId(bean.getObjectId() + "_exist");
            metric.setValue((double) bean.getExist());
            metric.setTime(bean.getTime());
            metric.setSource(bean);
            metricList.add(metric);
        }

        if (bean.getOther() > 0) {
            BaseMetric metric = new BaseMetric();
            metric.setMetricId(bean.getObjectId() + "_other");
            metric.setValue((double) bean.getOther());
            metric.setTime(bean.getTime());
            metric.setSource(bean);
            metricList.add(metric);
        }

        if (bean.getMetrics() > 0) {
            BaseMetric metric = new BaseMetric();
            metric.setMetricId(bean.getObjectId() + "_metrics");
            metric.setValue((double) bean.getMetrics());
            metric.setTime(bean.getTime());
            metric.setSource(bean);
            metricList.add(metric);
        }

        return metricList;
    }

    @Override
    public boolean isSupport(Realtime realtime) {
        return CollectRecordRealtime.class.equals(realtime.getClass());
    }
}
