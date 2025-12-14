package dwe.holding.admin.tenant;


import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@Configuration
public class MultiDataSourceConfig {

    @Bean(name = "globalDataSource")
    @ConfigurationProperties(prefix = "app.datasource.global")
    public DataSource globalDataSource() {
        return new HikariDataSource();
    }

    @Bean(name = "tenantDataSource")
    @Primary
    @ConfigurationProperties(prefix = "app.datasource.tenant")
    public DataSource tenantDataSource() {
        return new HikariDataSource();
    }
}