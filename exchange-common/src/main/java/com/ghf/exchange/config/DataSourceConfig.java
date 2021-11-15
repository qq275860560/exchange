package com.ghf.exchange.config;

import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;

import javax.annotation.Resource;
import javax.sql.DataSource;

/**
 * @author jiangyuanlin@163.com
 */
@Lazy
@Configuration
@Slf4j
public class DataSourceConfig {

    @Lazy
    @Resource
    private Environment environment;

    @Lazy
    @Bean
    @Primary
    public DataSource createDataSource() {
        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl(environment.getProperty("spring.datasource.url"));
        ds.setUsername(environment.getProperty("spring.datasource.username"));
        ds.setPassword(environment.getProperty("spring.datasource.password"));
        ds.setDriverClassName(environment.getProperty("spring.datasource.driverClassName"));

        ds.addDataSourceProperty("remarks", "true");
        ds.addDataSourceProperty("useInformationSchema", "true");
        ds.addDataSourceProperty("useCursorFetch", "true");
        ds.addDataSourceProperty("defaultFetchSize", 1000);
        ds.addDataSourceProperty("rewriteBatchedStatements", "true");
        ds.addDataSourceProperty("allowMultiQueries", "true");
        ds.addDataSourceProperty("nullCatalogMeansCurrent", "true");

        return ds;
    }

}
