package com.kopo_team4.kbbank_backend.domain.product.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductListResponse {
    private List<ProductResponse> products;
    private long totalElements;
    private int totalPages;
    private int currentPage;
    private int pageSize;

    public static ProductListResponse from(Page<ProductResponse> productPage) {
        return ProductListResponse.builder()
                .products(productPage.getContent())
                .totalElements(productPage.getTotalElements())
                .totalPages(productPage.getTotalPages())
                .currentPage(productPage.getNumber())
                .pageSize(productPage.getSize())
                .build();
    }

    public static ProductListResponse from(List<ProductResponse> products) {
        return ProductListResponse.builder()
                .products(products)
                .totalElements(products.size())
                .totalPages(1)
                .currentPage(0)
                .pageSize(products.size())
                .build();
    }
}
