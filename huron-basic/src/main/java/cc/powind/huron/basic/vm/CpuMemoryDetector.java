package cc.powind.huron.basic.vm;

import cc.powind.huron.core.model.BaseMetric;
import cc.powind.huron.core.model.Metric;
import cc.powind.huron.core.model.MetricDetector;
import cc.powind.huron.core.model.Realtime;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CpuMemoryDetector implements MetricDetector {

    @Override
    public Collection<? extends Metric> detect(Realtime realtime) {

        CpuMemory cpuMemory = (CpuMemory) realtime;

        List<BaseMetric> metricList = new ArrayList<>();

        {
            BaseMetric metric = new BaseMetric();
            metric.setMetricId("vm_cpu_used_" + realtime.getObjectId());
            metric.setValue(100 - cpuMemory.getId());
            metric.setTime(realtime.getTime());
            metric.setSource(realtime);
            metricList.add(metric);
        }

        {
            BaseMetric metric = new BaseMetric();
            metric.setMetricId("vm_memory_used_" + realtime.getObjectId());
            metric.setValue(cpuMemory.getUsed() * 100 / (double) cpuMemory.getTotal());
            metric.setTime(realtime.getTime());
            metric.setSource(realtime);
            metricList.add(metric);
        }

        return metricList;
    }

    @Override
    public boolean isSupport(Realtime realtime) {
        return realtime.getClass().equals(CpuMemory.class);
    }
}
