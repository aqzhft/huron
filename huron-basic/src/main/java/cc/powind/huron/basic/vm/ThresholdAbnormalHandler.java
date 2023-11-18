package cc.powind.huron.basic.vm;

import cc.powind.huron.core.collect.AbnormalHandler;
import cc.powind.huron.core.collect.ThresholdAbnormal;
import cc.powind.huron.core.collect.ThresholdPolicy;
import cc.powind.huron.core.model.Abnormal;
import cc.powind.huron.core.model.Metric;
import cc.powind.huron.core.model.Realtime;
import cc.powind.huron.core.model.RealtimeMapper;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Collections;

public class ThresholdAbnormalHandler implements AbnormalHandler, RealtimeMapper {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final DataSource dataSource;

    public ThresholdAbnormalHandler(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void handle(Abnormal abnormal) {
        insertBatch(Collections.singletonList(abnormal));
    }

    @Override
    public boolean isSupport(Abnormal abnormal) {
        return ThresholdAbnormal.class.equals(abnormal.getClass());
    }

    @Override
    public boolean isSupport(Realtime realtime) {
        return ThresholdAbnormal.class.equals(realtime.getClass());
    }

    @Override
    public void insertBatch(Collection<Realtime> realtimeList) {
        try (Connection connection = dataSource.getConnection()) {

            PreparedStatement preparedStatement = connection.prepareStatement("insert into abnormal_threshold(policy_id, policy_name, metric_id, metric_name, value, threshold, source_id, time)values(?, ?, ?, ?, ?, ?, ?, ?)");
            for (Realtime realtime : realtimeList) {

                ThresholdAbnormal abnormal = (ThresholdAbnormal) realtime;
                ThresholdPolicy policy = abnormal.getPolicy();
                Metric metric = abnormal.getMetric();
                Realtime source = metric.getSource();

                preparedStatement.setObject(1, policy.getPolicyId());
                preparedStatement.setObject(2, policy.getPolicyName());
                preparedStatement.setObject(3, metric.getMetricId());
                preparedStatement.setObject(4, metric.getMetricId());
                preparedStatement.setObject(5, metric.getValue());
                preparedStatement.setObject(6, policy.getThreshold());
                preparedStatement.setObject(7, source.getRealtimeId());
                preparedStatement.setObject(8, abnormal.getTime().atZone(ZoneId.systemDefault()).toLocalDateTime().format(formatter));
                preparedStatement.addBatch();
            }

            preparedStatement.executeBatch();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
