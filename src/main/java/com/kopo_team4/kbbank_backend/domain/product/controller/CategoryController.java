package com.kopo_team4.kbbank_backend.domain.product.controller;

import com.kopo_team4.kbbank_backend.domain.product.dto.ProductCategoryResponse;
import com.kopo_team4.kbbank_backend.domain.product.service.ProductService;
import com.kopo_team4.kbbank_backend.global.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
@Slf4j
public class CategoryController {

    private final ProductService productService;

    /**
     * 모든 카테고리 조회 API
     * GET /api/v1/categories
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductCategoryResponse>>> getAllCategories() {
        log.info("모든 카테고리 조회 요청");
        
        try {
            List<ProductCategoryResponse> response = productService.getAllCategories();
            log.info("모든 카테고리 조회 성공 - 카테고리 수: {}", response.size());
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (Exception e) {
            log.error("모든 카테고리 조회 중 오류 발생", e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("모든 카테고리 조회 중 오류가 발생했습니다."));
        }
    }

    /**
     * 위험도별 카테고리 조회 API
     * GET /api/v1/categories/risk/{riskLevel}
     */
    @GetMapping("/risk/{riskLevel}")
    public ResponseEntity<ApiResponse<List<ProductCategoryResponse>>> getCategoriesByRiskLevel(@PathVariable String riskLevel) {
        log.info("위험도별 카테고리 조회 요청 - riskLevel: {}", riskLevel);
        
        try {
            List<ProductCategoryResponse> response = productService.getCategoriesByRiskLevel(riskLevel);
            log.info("위험도별 카테고리 조회 성공 - 카테고리 수: {}", response.size());
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (Exception e) {
            log.error("위험도별 카테고리 조회 중 오류 발생", e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("위험도별 카테고리 조회 중 오류가 발생했습니다."));
        }
    }
}
