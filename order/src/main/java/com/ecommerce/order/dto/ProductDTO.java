package com.ecommerce.order.dto;

import lombok.Data;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

import java.math.BigDecimal;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ProductDTO {
    private Long id;
    private String name;
    private BigDecimal price;
    private Integer stockQuantity;
}
