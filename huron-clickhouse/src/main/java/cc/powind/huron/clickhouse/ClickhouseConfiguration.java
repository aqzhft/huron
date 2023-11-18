package cc.powind.huron.clickhouse;

import com.clickhouse.jdbc.ClickHouseDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Properties;

@Configuration
@EnableConfigurationProperties(CkCustomProperties.class)
public class ClickhouseConfiguration {

    @Autowired
    private CkCustomProperties properties;

    @Bean
    public DataSource clickhouseDataSource() throws SQLException {
        String url = String.format("jdbc:clickhouse://%s:%s/%s", properties.getHost(), properties.getPort(), properties.getDatabase());
        Properties props = new Properties();
        props.put("user", properties.getUsername());
        props.put("password", properties.getPassword());
        return new ClickHouseDataSource(url, props);
    }
}
