package com.nadir.multitenant.service;

import com.nadir.multitenant.dto.ProductDto;
import com.nadir.multitenant.entity.Product;
import com.nadir.multitenant.repository.ProductRepository;
import com.nadir.multitenant.resolver.TenantContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public List<Product> getAll() {
        String tenant = TenantContext.getCurrentTenant();
        log.debug("Fetching products for tenant: {}", tenant);
        return productRepository.findAllByTenantId(tenant);
    }

    public Product getById(Long id) {
        String tenant = TenantContext.getCurrentTenant();
        return productRepository.findByIdAndTenantId(id, tenant)
                .orElseThrow(() -> new RuntimeException("Product not found for tenant: " + tenant));
    }

    @Transactional
    public Product create(ProductDto.CreateRequest req) {
        Product product = Product.builder()
                .name(req.getName())
                .description(req.getDescription())
                .price(req.getPrice())
                .stock(req.getStock())
                .build();
        return productRepository.save(product);
    }

    @Transactional
    public Product update(Long id, ProductDto.UpdateRequest req) {
        Product product = getById(id);
        if (req.getName() != null) product.setName(req.getName());
        if (req.getPrice() != null) product.setPrice(req.getPrice());
        if (req.getStock() != null) product.setStock(req.getStock());
        return productRepository.save(product);
    }

    @Transactional
    public void delete(Long id) {
        Product product = getById(id); // validates tenant ownership
        productRepository.delete(product);
    }

    public long count() {
        return productRepository.countByTenantId(TenantContext.getCurrentTenant());
    }
}
