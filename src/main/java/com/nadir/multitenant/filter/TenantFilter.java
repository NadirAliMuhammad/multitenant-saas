package com.nadir.multitenant.filter;

import com.nadir.multitenant.resolver.TenantContext;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Resolves tenant from:
 * 1. X-Tenant-ID header  (e.g. API clients)
 * 2. Subdomain           (e.g. acme.myapp.com → "acme")
 * 3. Falls back to "default"
 */
@Slf4j
@Component
@Order(1)
public class TenantFilter extends OncePerRequestFilter {

    private static final String TENANT_HEADER = "X-Tenant-ID";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        try {
            String tenant = resolveTenant(request);
            TenantContext.setCurrentTenant(tenant);
            log.debug("Resolved tenant: {}", tenant);
            chain.doFilter(request, response);
        } finally {
            TenantContext.clear(); // CRITICAL: always clear to prevent thread reuse leaks
        }
    }

    private String resolveTenant(HttpServletRequest request) {
        // 1. Check explicit header
        String header = request.getHeader(TENANT_HEADER);
        if (StringUtils.hasText(header)) return header.toLowerCase().trim();

        // 2. Extract from subdomain
        String host = request.getServerName();
        if (host != null && host.contains(".")) {
            String subdomain = host.split("\\.")[0];
            if (!subdomain.equals("www") && !subdomain.equals("api")) return subdomain;
        }

        // 3. Default
        return "default";
    }
}
