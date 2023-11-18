package cc.powind.huron.clickhouse;

import cc.powind.huron.core.model.BaseMetric;
import cc.powind.huron.core.model.Metric;
import cc.powind.huron.core.model.MetricHandler;
import cc.powind.huron.rectifier.Rectifier;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collection;

/**
 * CREATE TABLE IF NOT EXISTS realtime_metric (metric_id String, value Float64, source_id String, time Datetime) ENGINE = MergeTree() PARTITION BY toYYYYMM(time) ORDER BY (metric_id, time);
 */
public class MetricPersistenceMapper implements MetricHandler {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private DataSource dataSource;

    private Rectifier<BaseMetric> rectifier;

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Rectifier<BaseMetric> getRectifier() {
        return rectifier;
    }

    public void setRectifier(Rectifier<BaseMetric> rectifier) {
        this.rectifier = rectifier;
    }

    public void init() {
        rectifier.outflow(this::batchSave);
    }

    @Override
    public void handle(Metric metric) {
        rectifier.inflow((BaseMetric) metric);
    }

    protected void batchSave(Collection<BaseMetric> metricList) {

        try (Connection connection = dataSource.getConnection()) {

            PreparedStatement preparedStatement = connection.prepareStatement("insert into realtime_metric(metric_id, value, source_id, time)values(?, ?, ?, ?)");
            for (BaseMetric metric : metricList) {

                preparedStatement.setObject(1, metric.getMetricId());
                preparedStatement.setObject(2, metric.getValue());
                preparedStatement.setObject(3, metric.getSource().getRealtimeId());
                preparedStatement.setObject(4, metric.getTime().atZone(ZoneId.systemDefault()).toLocalDateTime().format(formatter));
                preparedStatement.addBatch();
            }

            preparedStatement.executeBatch();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isSupport(Metric metric) {
        return BaseMetric.class.equals(metric.getClass());
    }
}
