package com.nadir.multitenant.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;

public class ProductDto {

    @Data
    public static class CreateRequest {
        @NotBlank
        private String name;
        private String description;
        @NotNull @DecimalMin("0.0")
        private BigDecimal price;
        @Min(0)
        private int stock;
    }

    @Data
    public static class UpdateRequest {
        private String name;
        private BigDecimal price;
        private Integer stock;
    }
}
