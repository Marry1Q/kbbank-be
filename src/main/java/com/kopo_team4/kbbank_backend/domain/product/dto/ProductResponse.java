package com.kopo_team4.kbbank_backend.domain.product.dto;

import com.kopo_team4.kbbank_backend.domain.product.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductResponse {
    private Long productId;
    private String externalProductId;
    private String productCode;
    private String productName;
    private String productSubName;
    private ProductCategoryResponse category;
    private String productType;
    
    // 수익률 관련
    private BigDecimal baseRate;
    private BigDecimal maxRate;
    private BigDecimal expectedReturnRate;
    private BigDecimal actualReturnRate;
    
    // 투자 관련
    private BigDecimal minInvestmentAmount;
    private BigDecimal maxInvestmentAmount;
    private BigDecimal monthlyInvestmentAmount;
    
    // 기간 관련
    private Integer minPeriodMonths;
    private Integer maxPeriodMonths;
    private Integer maturityPeriodMonths;
    
    // 위험도 및 특성
    private String riskLevel;
    private Integer riskScore;
    private String volatilityLevel;
    
    // 상품 특성
    private Boolean isTaxFree;
    private Boolean isGuaranteed;
    private Boolean isCompoundInterest;
    private Boolean isMonthlyInterest;
    
    // 상품 상태
    private String productStatus;
    private LocalDate salesStartDate;
    private LocalDate salesEndDate;
    
    // 상품 설명
    private String productDescription;
    private String productFeatures;
    private String investmentStrategy;

    public static ProductResponse from(Product product) {
        return ProductResponse.builder()
                .productId(product.getProductId())
                .externalProductId(product.getExternalProductId())
                .productCode(product.getProductCode())
                .productName(product.getProductName())
                .productSubName(product.getProductSubName())
                .category(ProductCategoryResponse.from(product.getCategory()))
                .productType(product.getProductType())
                .baseRate(product.getBaseRate())
                .maxRate(product.getMaxRate())
                .expectedReturnRate(product.getExpectedReturnRate())
                .actualReturnRate(product.getActualReturnRate())
                .minInvestmentAmount(product.getMinInvestmentAmount())
                .maxInvestmentAmount(product.getMaxInvestmentAmount())
                .monthlyInvestmentAmount(product.getMonthlyInvestmentAmount())
                .minPeriodMonths(product.getMinPeriodMonths())
                .maxPeriodMonths(product.getMaxPeriodMonths())
                .maturityPeriodMonths(product.getMaturityPeriodMonths())
                .riskLevel(product.getRiskLevel())
                .riskScore(product.getRiskScore())
                .volatilityLevel(product.getVolatilityLevel())
                .isTaxFree(product.getIsTaxFree())
                .isGuaranteed(product.getIsGuaranteed())
                .isCompoundInterest(product.getIsCompoundInterest())
                .isMonthlyInterest(product.getIsMonthlyInterest())
                .productStatus(product.getProductStatus())
                .salesStartDate(product.getSalesStartDate())
                .salesEndDate(product.getSalesEndDate())
                .productDescription(product.getProductDescription())
                .productFeatures(product.getProductFeatures())
                .investmentStrategy(product.getInvestmentStrategy())
                .build();
    }
}
