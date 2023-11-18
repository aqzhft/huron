package cc.powind.huron.clickhouse;

import cc.powind.huron.core.model.Realtime;
import cc.powind.huron.core.model.RealtimeMapper;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.Collection;

public abstract class ClickhouseRealtimeMapper implements RealtimeMapper {

    private DataSource dataSource;

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void insertBatch(Collection<Realtime> realtimeList) {

        try (Connection connection = dataSource.getConnection()) {

            // how to deal with

        } catch (Exception e) {

        }
    }
}
