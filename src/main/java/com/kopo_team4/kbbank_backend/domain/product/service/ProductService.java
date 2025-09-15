package com.kopo_team4.kbbank_backend.domain.product.service;

import com.kopo_team4.kbbank_backend.domain.product.dto.ProductCategoryResponse;
import com.kopo_team4.kbbank_backend.domain.product.dto.ProductListResponse;
import com.kopo_team4.kbbank_backend.domain.product.dto.ProductResponse;
import com.kopo_team4.kbbank_backend.domain.product.entity.Product;
import com.kopo_team4.kbbank_backend.domain.product.entity.ProductCategory;
import com.kopo_team4.kbbank_backend.domain.product.repository.ProductCategoryRepository;
import com.kopo_team4.kbbank_backend.domain.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductCategoryRepository productCategoryRepository;

    /**
     * 모든 상품 조회 (페이징)
     */
    public ProductListResponse getAllProducts(String category, String riskLevel, String productType,
                                            BigDecimal minAmount, BigDecimal maxAmount,
                                            int page, int size) {
        log.info("상품 목록 조회 요청 - category: {}, riskLevel: {}, productType: {}, page: {}, size: {}", 
                category, riskLevel, productType, page, size);

        Pageable pageable = PageRequest.of(page, size);
        Page<Product> productPage = productRepository.findProductsByConditions(
                category, riskLevel, productType, minAmount, maxAmount, pageable);

        Page<ProductResponse> responsePage = productPage.map(ProductResponse::from);
        return ProductListResponse.from(responsePage);
    }

    /**
     * 특정 상품 상세 조회
     */
    public ProductResponse getProductById(Long productId) {
        log.info("상품 상세 조회 요청 - productId: {}", productId);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품입니다. productId: " + productId));

        return ProductResponse.from(product);
    }

    /**
     * 외부 상품 ID로 상품 조회
     */
    public ProductResponse getProductByExternalId(String externalProductId) {
        log.info("외부 상품 ID로 상품 조회 요청 - externalProductId: {}", externalProductId);

        Product product = productRepository.findByExternalProductId(externalProductId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품입니다. externalProductId: " + externalProductId));

        return ProductResponse.from(product);
    }

    /**
     * 카테고리별 상품 조회
     */
    public ProductListResponse getProductsByCategory(String categoryCode) {
        log.info("카테고리별 상품 조회 요청 - categoryCode: {}", categoryCode);

        List<Product> products = productRepository.findActiveProductsByCategory(categoryCode);
        List<ProductResponse> productResponses = products.stream()
                .map(ProductResponse::from)
                .collect(Collectors.toList());

        return ProductListResponse.from(productResponses);
    }

    /**
     * 위험도별 상품 조회
     */
    public ProductListResponse getProductsByRiskLevel(String riskLevel) {
        log.info("위험도별 상품 조회 요청 - riskLevel: {}", riskLevel);

        List<Product> products = productRepository.findActiveProductsByRiskLevel(riskLevel);
        List<ProductResponse> productResponses = products.stream()
                .map(ProductResponse::from)
                .collect(Collectors.toList());

        return ProductListResponse.from(productResponses);
    }

    /**
     * 상품 유형별 조회
     */
    public ProductListResponse getProductsByType(String productType) {
        log.info("상품 유형별 조회 요청 - productType: {}", productType);

        List<Product> products = productRepository.findActiveProductsByType(productType);
        List<ProductResponse> productResponses = products.stream()
                .map(ProductResponse::from)
                .collect(Collectors.toList());

        return ProductListResponse.from(productResponses);
    }

    /**
     * 최소 수익률 기준 상품 조회
     */
    public ProductListResponse getProductsByMinReturnRate(BigDecimal minRate) {
        log.info("최소 수익률 기준 상품 조회 요청 - minRate: {}", minRate);

        List<Product> products = productRepository.findActiveProductsByMinReturnRate(minRate);
        List<ProductResponse> productResponses = products.stream()
                .map(ProductResponse::from)
                .collect(Collectors.toList());

        return ProductListResponse.from(productResponses);
    }

    /**
     * 투자 금액 기준 상품 조회
     */
    public ProductListResponse getProductsByInvestmentAmount(BigDecimal amount) {
        log.info("투자 금액 기준 상품 조회 요청 - amount: {}", amount);

        List<Product> products = productRepository.findActiveProductsByInvestmentAmount(amount);
        List<ProductResponse> productResponses = products.stream()
                .map(ProductResponse::from)
                .collect(Collectors.toList());

        return ProductListResponse.from(productResponses);
    }

    /**
     * 모든 활성 상품 조회
     */
    public ProductListResponse getAllActiveProducts() {
        log.info("모든 활성 상품 조회 요청");

        List<Product> products = productRepository.findAllActiveProducts();
        List<ProductResponse> productResponses = products.stream()
                .map(ProductResponse::from)
                .collect(Collectors.toList());

        return ProductListResponse.from(productResponses);
    }

    /**
     * 모든 카테고리 조회
     */
    public List<ProductCategoryResponse> getAllCategories() {
        log.info("모든 카테고리 조회 요청");

        List<ProductCategory> categories = productCategoryRepository.findAllByOrderByCategoryCode();
        return categories.stream()
                .map(ProductCategoryResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 위험도별 카테고리 조회
     */
    public List<ProductCategoryResponse> getCategoriesByRiskLevel(String riskLevel) {
        log.info("위험도별 카테고리 조회 요청 - riskLevel: {}", riskLevel);

        List<ProductCategory> categories = productCategoryRepository.findByRiskLevel(riskLevel);
        return categories.stream()
                .map(ProductCategoryResponse::from)
                .collect(Collectors.toList());
    }
}
