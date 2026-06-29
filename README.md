# Multi-Tenant SaaS Platform

A production-grade **multi-tenant SaaS backend** built with Spring Boot 3, demonstrating schema-based tenant isolation — the architecture used by companies like Salesforce, Shopify, and HubSpot.

## What is Multi-Tenancy?

Multiple customers (tenants) share the same application but their data is fully isolated.

```
Request: X-Tenant-ID: acme
    → TenantFilter resolves "acme"
    → TenantContext stores in ThreadLocal
    → MultiTenantDataSource routes to acme_db
    → Queries only return acme's data
    → ThreadLocal cleared after request
```

## Isolation Strategies

| Strategy | This Project | Description |
|----------|-------------|-------------|
| Schema-per-tenant | ✅ | Each tenant gets own DB schema |
| Row-level isolation | ✅ | tenantId stamped on every entity |
| DataSource routing | ✅ | AbstractRoutingDataSource |

## Architecture

```
HTTP Request
    ↓
TenantFilter (resolves X-Tenant-ID header or subdomain)
    ↓
TenantContext (ThreadLocal storage)
    ↓
MultiTenantDataSource (routes to correct schema)
    ↓
Repository (always filters by tenantId)
    ↓
TenantAwareEntity (auto-stamps tenantId on persist)
```

## Features

- ✅ Tenant resolution from header (`X-Tenant-ID`) or subdomain
- ✅ ThreadLocal tenant context with guaranteed cleanup
- ✅ `AbstractRoutingDataSource` for per-tenant DB routing
- ✅ `TenantAwareEntity` base class — auto-stamps tenantId
- ✅ Tenant-scoped repositories — no cross-tenant leakage possible
- ✅ Docker support

## Running

```bash
./mvnw spring-boot:run
# or
docker-compose up --build
```

## API Usage

All requests require `X-Tenant-ID` header:

### Tenant: acme
```bash
# Create product for acme
curl -X POST http://localhost:8082/api/products \
  -H "X-Tenant-ID: acme" \
  -H "Content-Type: application/json" \
  -d '{"name":"Widget A","price":9.99,"stock":100}'

# Get acme's products (will NOT return globex products)
curl http://localhost:8082/api/products \
  -H "X-Tenant-ID: acme"
```

### Tenant: globex
```bash
curl -X POST http://localhost:8082/api/products \
  -H "X-Tenant-ID: globex" \
  -H "Content-Type: application/json" \
  -d '{"name":"Gadget Z","price":49.99,"stock":50}'

# Isolation proof: acme cannot see globex data
curl http://localhost:8082/api/products -H "X-Tenant-ID: acme"
# → returns only acme products
```

### Check current tenant
```bash
curl http://localhost:8082/api/tenant/info -H "X-Tenant-ID: acme"
# → {"currentTenant":"acme","message":"You are operating in tenant: acme"}
```

## Production Considerations

- Load tenant configs from database, not code
- Add tenant validation middleware (reject unknown tenants)
- Use connection pooling per tenant (HikariCP)
- Add tenant-level rate limiting
- Encrypt sensitive tenant data at rest

## Author
**Muhammad Nadir** — [LinkedIn](https://linkedin.com/in/muhammad-nadir-26095646)
