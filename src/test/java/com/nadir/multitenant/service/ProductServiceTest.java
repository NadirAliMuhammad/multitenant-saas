package com.nadir.multitenant.service;

import com.nadir.multitenant.dto.ProductDto;
import com.nadir.multitenant.entity.Product;
import com.nadir.multitenant.repository.ProductRepository;
import com.nadir.multitenant.resolver.TenantContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @BeforeEach
    void setTenant() {
        TenantContext.setCurrentTenant("acme");
    }

    @AfterEach
    void cleanup() {
        TenantContext.clear();
    }

    @Test
    void getAll_queriesScopedToCurrentTenant() {
        when(productRepository.findAllByTenantId("acme")).thenReturn(List.of(new Product()));

        List<Product> result = productService.getAll();

        assertThat(result).hasSize(1);
        // Verifies the service never calls findAll() — only the tenant-scoped query.
        ArgumentCaptor<String> tenant = ArgumentCaptor.forClass(String.class);
        org.mockito.Mockito.verify(productRepository).findAllByTenantId(tenant.capture());
        assertThat(tenant.getValue()).isEqualTo("acme");
    }

    @Test
    void getById_throws_whenProductNotFoundForTenant() {
        when(productRepository.findByIdAndTenantId(99L, "acme")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.getById(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("acme");
    }

    @Test
    void create_persistsProductBuiltFromRequest() {
        ProductDto.CreateRequest req = new ProductDto.CreateRequest();
        req.setName("Widget A");
        req.setPrice(new BigDecimal("9.99"));
        req.setStock(100);
        when(productRepository.save(any(Product.class))).thenAnswer(inv -> inv.getArgument(0));

        Product saved = productService.create(req);

        assertThat(saved.getName()).isEqualTo("Widget A");
        assertThat(saved.getPrice()).isEqualByComparingTo("9.99");
        assertThat(saved.getStock()).isEqualTo(100);
    }
}
