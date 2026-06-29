package com.nadir.multitenant.entity;

import com.nadir.multitenant.resolver.TenantContext;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import lombok.Getter;

/**
 * Base entity that automatically stamps tenantId on every persist.
 * All tenant-scoped entities extend this — ensures no cross-tenant leakage.
 */
@MappedSuperclass
@Getter
public abstract class TenantAwareEntity {

    @Column(name = "tenant_id", nullable = false, updatable = false)
    private String tenantId;

    @PrePersist
    protected void setTenant() {
        if (this.tenantId == null) {
            this.tenantId = TenantContext.getCurrentTenant();
        }
    }
}
