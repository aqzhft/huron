package cc.powind.huron.core.collect;

import java.util.List;

@FunctionalInterface
public interface ThresholdPolicyService {

    List<ThresholdPolicy> load(String metricId);
}
