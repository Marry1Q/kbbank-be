package com.kopo_team4.kbbank_backend.domain.product.controller;

import com.kopo_team4.kbbank_backend.domain.product.dto.ProductListResponse;
import com.kopo_team4.kbbank_backend.domain.product.dto.ProductResponse;
import com.kopo_team4.kbbank_backend.domain.product.service.ProductService;
import com.kopo_team4.kbbank_backend.global.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Slf4j
public class ProductController {

    private final ProductService productService;

    /**
     * 모든 상품 조회 API
     * GET /api/v1/products
     */
    @GetMapping
    public ResponseEntity<ApiResponse<ProductListResponse>> getAllProducts(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String riskLevel,
            @RequestParam(required = false) String productType,
            @RequestParam(required = false) BigDecimal minAmount,
            @RequestParam(required = false) BigDecimal maxAmount,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        log.info("상품 목록 조회 요청 - category: {}, riskLevel: {}, productType: {}, page: {}, size: {}", 
                category, riskLevel, productType, page, size);
        
        try {
            ProductListResponse response = productService.getAllProducts(
                    category, riskLevel, productType, minAmount, maxAmount, page, size);
            log.info("상품 목록 조회 성공 - 총 상품 수: {}", response.getTotalElements());
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (Exception e) {
            log.error("상품 목록 조회 중 오류 발생", e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("상품 목록 조회 중 오류가 발생했습니다."));
        }
    }

    /**
     * 특정 상품 상세 조회 API
     * GET /api/v1/products/{productId}
     */
    @GetMapping("/{productId}")
    public ResponseEntity<ApiResponse<ProductResponse>> getProductById(@PathVariable Long productId) {
        log.info("상품 상세 조회 요청 - productId: {}", productId);
        
        try {
            ProductResponse response = productService.getProductById(productId);
            log.info("상품 상세 조회 성공 - 상품명: {}", response.getProductName());
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (IllegalArgumentException e) {
            log.warn("상품 상세 조회 실패 - 존재하지 않는 상품: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("상품 상세 조회 중 오류 발생", e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("상품 상세 조회 중 오류가 발생했습니다."));
        }
    }

    /**
     * 외부 상품 ID로 상품 조회 API
     * GET /api/v1/products/external/{externalProductId}
     */
    @GetMapping("/external/{externalProductId}")
    public ResponseEntity<ApiResponse<ProductResponse>> getProductByExternalId(@PathVariable String externalProductId) {
        log.info("외부 상품 ID로 상품 조회 요청 - externalProductId: {}", externalProductId);
        
        try {
            ProductResponse response = productService.getProductByExternalId(externalProductId);
            log.info("외부 상품 ID로 상품 조회 성공 - 상품명: {}", response.getProductName());
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (IllegalArgumentException e) {
            log.warn("외부 상품 ID로 상품 조회 실패 - 존재하지 않는 상품: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("외부 상품 ID로 상품 조회 중 오류 발생", e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("외부 상품 ID로 상품 조회 중 오류가 발생했습니다."));
        }
    }

    /**
     * 카테고리별 상품 조회 API
     * GET /api/v1/products/category/{categoryCode}
     */
    @GetMapping("/category/{categoryCode}")
    public ResponseEntity<ApiResponse<ProductListResponse>> getProductsByCategory(@PathVariable String categoryCode) {
        log.info("카테고리별 상품 조회 요청 - categoryCode: {}", categoryCode);
        
        try {
            ProductListResponse response = productService.getProductsByCategory(categoryCode);
            log.info("카테고리별 상품 조회 성공 - 상품 수: {}", response.getTotalElements());
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (Exception e) {
            log.error("카테고리별 상품 조회 중 오류 발생", e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("카테고리별 상품 조회 중 오류가 발생했습니다."));
        }
    }

    /**
     * 위험도별 상품 조회 API
     * GET /api/v1/products/risk/{riskLevel}
     */
    @GetMapping("/risk/{riskLevel}")
    public ResponseEntity<ApiResponse<ProductListResponse>> getProductsByRiskLevel(@PathVariable String riskLevel) {
        log.info("위험도별 상품 조회 요청 - riskLevel: {}", riskLevel);
        
        try {
            ProductListResponse response = productService.getProductsByRiskLevel(riskLevel);
            log.info("위험도별 상품 조회 성공 - 상품 수: {}", response.getTotalElements());
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (Exception e) {
            log.error("위험도별 상품 조회 중 오류 발생", e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("위험도별 상품 조회 중 오류가 발생했습니다."));
        }
    }

    /**
     * 상품 유형별 조회 API
     * GET /api/v1/products/type/{productType}
     */
    @GetMapping("/type/{productType}")
    public ResponseEntity<ApiResponse<ProductListResponse>> getProductsByType(@PathVariable String productType) {
        log.info("상품 유형별 조회 요청 - productType: {}", productType);
        
        try {
            ProductListResponse response = productService.getProductsByType(productType);
            log.info("상품 유형별 조회 성공 - 상품 수: {}", response.getTotalElements());
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (Exception e) {
            log.error("상품 유형별 조회 중 오류 발생", e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("상품 유형별 조회 중 오류가 발생했습니다."));
        }
    }

    /**
     * 최소 수익률 기준 상품 조회 API
     * GET /api/v1/products/return-rate/{minRate}
     */
    @GetMapping("/return-rate/{minRate}")
    public ResponseEntity<ApiResponse<ProductListResponse>> getProductsByMinReturnRate(@PathVariable BigDecimal minRate) {
        log.info("최소 수익률 기준 상품 조회 요청 - minRate: {}", minRate);
        
        try {
            ProductListResponse response = productService.getProductsByMinReturnRate(minRate);
            log.info("최소 수익률 기준 상품 조회 성공 - 상품 수: {}", response.getTotalElements());
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (Exception e) {
            log.error("최소 수익률 기준 상품 조회 중 오류 발생", e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("최소 수익률 기준 상품 조회 중 오류가 발생했습니다."));
        }
    }

    /**
     * 투자 금액 기준 상품 조회 API
     * GET /api/v1/products/investment-amount/{amount}
     */
    @GetMapping("/investment-amount/{amount}")
    public ResponseEntity<ApiResponse<ProductListResponse>> getProductsByInvestmentAmount(@PathVariable BigDecimal amount) {
        log.info("투자 금액 기준 상품 조회 요청 - amount: {}", amount);
        
        try {
            ProductListResponse response = productService.getProductsByInvestmentAmount(amount);
            log.info("투자 금액 기준 상품 조회 성공 - 상품 수: {}", response.getTotalElements());
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (Exception e) {
            log.error("투자 금액 기준 상품 조회 중 오류 발생", e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("투자 금액 기준 상품 조회 중 오류가 발생했습니다."));
        }
    }

    /**
     * 모든 활성 상품 조회 API
     * GET /api/v1/products/active
     */
    @GetMapping("/active")
    public ResponseEntity<ApiResponse<ProductListResponse>> getAllActiveProducts() {
        log.info("모든 활성 상품 조회 요청");
        
        try {
            ProductListResponse response = productService.getAllActiveProducts();
            log.info("모든 활성 상품 조회 성공 - 상품 수: {}", response.getTotalElements());
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (Exception e) {
            log.error("모든 활성 상품 조회 중 오류 발생", e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("모든 활성 상품 조회 중 오류가 발생했습니다."));
        }
    }


}
