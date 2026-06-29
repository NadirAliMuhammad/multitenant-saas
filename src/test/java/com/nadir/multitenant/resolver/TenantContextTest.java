package com.nadir.multitenant.resolver;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TenantContextTest {

    @AfterEach
    void cleanup() {
        TenantContext.clear();
    }

    @Test
    void setAndGet_returnsCurrentTenant() {
        TenantContext.setCurrentTenant("acme");

        assertThat(TenantContext.getCurrentTenant()).isEqualTo("acme");
        assertThat(TenantContext.hasTenant()).isTrue();
    }

    @Test
    void clear_removesTenant() {
        TenantContext.setCurrentTenant("acme");

        TenantContext.clear();

        assertThat(TenantContext.getCurrentTenant()).isNull();
        assertThat(TenantContext.hasTenant()).isFalse();
    }

    @Test
    void hasTenant_isFalse_whenBlank() {
        TenantContext.setCurrentTenant("   ");

        assertThat(TenantContext.hasTenant()).isFalse();
    }
}
