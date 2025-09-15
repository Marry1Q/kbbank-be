package com.kopo_team4.kbbank_backend.domain.product.dto;

import com.kopo_team4.kbbank_backend.domain.product.entity.ProductCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductCategoryResponse {
    private Long categoryId;
    private String categoryCode;
    private String categoryName;
    private String categoryDescription;
    private String riskLevel;

    public static ProductCategoryResponse from(ProductCategory category) {
        return ProductCategoryResponse.builder()
                .categoryId(category.getCategoryId())
                .categoryCode(category.getCategoryCode())
                .categoryName(category.getCategoryName())
                .categoryDescription(category.getCategoryDescription())
                .riskLevel(category.getRiskLevel())
                .build();
    }
}
