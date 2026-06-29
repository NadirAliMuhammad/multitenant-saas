package com.nadir.multitenant.config;

import com.nadir.multitenant.resolver.TenantContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * Routes database connections to the correct tenant schema.
 *
 * Spring calls determineCurrentLookupKey() before every query.
 * We return the current tenant ID, which maps to the correct DataSource.
 *
 * Schema isolation strategy: each tenant gets their own DB schema
 * (e.g. tenant_acme, tenant_globex) — data is fully isolated.
 */
@Slf4j
public class MultiTenantDataSource extends AbstractRoutingDataSource {

    @Override
    protected Object determineCurrentLookupKey() {
        String tenant = TenantContext.getCurrentTenant();
        log.debug("Routing to tenant datasource: {}", tenant);
        return tenant != null ? tenant : "default";
    }
}
