package com.nadir.multitenant.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class DataSourceConfig {

    /**
     * In production: load tenant configs from DB or config service.
     * Here we define them statically for demo clarity.
     */
    @Bean
    @Primary
    public DataSource dataSource() {
        MultiTenantDataSource multiTenantDS = new MultiTenantDataSource();

        Map<Object, Object> dataSources = new HashMap<>();

        // Default / public schema
        dataSources.put("default", buildDataSource("jdbc:h2:mem:default_db;DB_CLOSE_DELAY=-1", "default"));

        // Tenant: acme
        dataSources.put("acme", buildDataSource("jdbc:h2:mem:acme_db;DB_CLOSE_DELAY=-1", "acme"));

        // Tenant: globex
        dataSources.put("globex", buildDataSource("jdbc:h2:mem:globex_db;DB_CLOSE_DELAY=-1", "globex"));

        multiTenantDS.setTargetDataSources(dataSources);
        multiTenantDS.setDefaultTargetDataSource(dataSources.get("default"));
        multiTenantDS.afterPropertiesSet();

        return multiTenantDS;
    }

    private DataSource buildDataSource(String url, String schema) {
        DriverManagerDataSource ds = new DriverManagerDataSource();
        ds.setDriverClassName("org.h2.Driver");
        ds.setUrl(url);
        ds.setUsername("sa");
        ds.setPassword("");
        return ds;
    }
}
