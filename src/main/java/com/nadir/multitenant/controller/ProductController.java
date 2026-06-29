package com.nadir.multitenant.controller;

import com.nadir.multitenant.dto.ProductDto;
import com.nadir.multitenant.entity.Product;
import com.nadir.multitenant.resolver.TenantContext;
import com.nadir.multitenant.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<List<Product>> getAll() {
        return ResponseEntity.ok(productService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getById(id));
    }

    @PostMapping
    public ResponseEntity<Product> create(@Valid @RequestBody ProductDto.CreateRequest req) {
        return ResponseEntity.ok(productService.create(req));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> update(@PathVariable Long id,
                                          @RequestBody ProductDto.UpdateRequest req) {
        return ResponseEntity.ok(productService.update(id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

// ── Tenant Info ───────────────────────────────────────────────────────────────

class TenantController {

    @RestController
    @RequestMapping("/api/tenant")
    static class Inner {

        @GetMapping("/info")
        public ResponseEntity<Map<String, Object>> info() {
            String tenant = TenantContext.getCurrentTenant();
            return ResponseEntity.ok(Map.of(
                    "currentTenant", tenant,
                    "message", "You are operating in tenant: " + tenant
            ));
        }
    }
}
