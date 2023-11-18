package cc.powind.huron.basic.vm;

import cc.powind.huron.core.model.Realtime;
import cc.powind.huron.core.model.RealtimeMapper;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collection;

public class CpuMemoryMapper implements RealtimeMapper {

    private final DataSource dataSource;

    public CpuMemoryMapper(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public boolean isSupport(Realtime realtime) {
        return CpuMemory.class.equals(realtime.getClass());
    }

    @Override
    public void insertBatch(Collection<Realtime> realtimeList) {
        try (Connection connection = dataSource.getConnection()) {

            PreparedStatement preparedStatement = connection.prepareStatement("insert into cpu_memory_record(unique_id, total, free, used, buff, us, sy, id, time)values(?, ?, ?, ?, ?, ?, ?, ?, ?)");
            for (Realtime realtime : realtimeList) {

                CpuMemory cpuMemory = (CpuMemory) realtime;

                preparedStatement.setObject(1, cpuMemory.getUniqueId());
                preparedStatement.setObject(2, cpuMemory.getTotal());
                preparedStatement.setObject(3, cpuMemory.getFree());
                preparedStatement.setObject(4, cpuMemory.getUsed());
                preparedStatement.setObject(5, cpuMemory.getBuff());
                preparedStatement.setObject(6, cpuMemory.getUs());
                preparedStatement.setObject(7, cpuMemory.getSy());
                preparedStatement.setObject(8, cpuMemory.getId());
                preparedStatement.setObject(9, cpuMemory.getTime().atZone(ZoneId.systemDefault()).toLocalDateTime().format(formatter));
                preparedStatement.addBatch();
            }

            preparedStatement.executeBatch();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
