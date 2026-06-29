package com.nadir.multitenant.resolver;

/**
 * Holds the current tenant identifier in a ThreadLocal.
 * Every request sets this at the filter layer and clears it after.
 *
 * This is the heart of multi-tenancy — all downstream components
 * read from here to know which tenant they're serving.
 */
public class TenantContext {

    private static final ThreadLocal<String> CURRENT_TENANT = new InheritableThreadLocal<>();

    private TenantContext() {}

    public static String getCurrentTenant() {
        return CURRENT_TENANT.get();
    }

    public static void setCurrentTenant(String tenant) {
        CURRENT_TENANT.set(tenant);
    }

    public static void clear() {
        CURRENT_TENANT.remove();
    }

    public static boolean hasTenant() {
        String tenant = CURRENT_TENANT.get();
        return tenant != null && !tenant.isBlank();
    }
}
