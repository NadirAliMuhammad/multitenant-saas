package com.nadir.multitenant.repository;

import com.nadir.multitenant.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    // Always filter by tenantId — prevents cross-tenant data leakage
    List<Product> findAllByTenantId(String tenantId);

    Optional<Product> findByIdAndTenantId(Long id, String tenantId);

    @Query("SELECT p FROM Product p WHERE p.tenantId = :tenantId AND p.stock > 0")
    List<Product> findAvailableByTenant(String tenantId);

    long countByTenantId(String tenantId);
}
