package com.kopo_team4.kbbank_backend.domain.product.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "kb_bank_products")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long productId;

    @Column(name = "external_product_id", nullable = false, unique = true, length = 50)
    private String externalProductId;

    @Column(name = "product_code", nullable = false, length = 50)
    private String productCode;

    @Column(name = "product_name", nullable = false, length = 200)
    private String productName;

    @Column(name = "product_sub_name", length = 100)
    private String productSubName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private ProductCategory category;

    @Column(name = "product_type", nullable = false, length = 50)
    private String productType;

    // 수익률 관련
    @Column(name = "base_rate", precision = 5, scale = 2)
    private BigDecimal baseRate;

    @Column(name = "max_rate", precision = 5, scale = 2)
    private BigDecimal maxRate;

    @Column(name = "expected_return_rate", precision = 5, scale = 2)
    private BigDecimal expectedReturnRate;

    @Column(name = "actual_return_rate", precision = 5, scale = 2)
    private BigDecimal actualReturnRate;

    // 투자 관련
    @Column(name = "min_investment_amount", precision = 15, scale = 2)
    private BigDecimal minInvestmentAmount;

    @Column(name = "max_investment_amount", precision = 15, scale = 2)
    private BigDecimal maxInvestmentAmount;

    @Column(name = "monthly_investment_amount", precision = 15, scale = 2)
    private BigDecimal monthlyInvestmentAmount;

    // 기간 관련
    @Column(name = "min_period_months")
    private Integer minPeriodMonths;

    @Column(name = "max_period_months")
    private Integer maxPeriodMonths;

    @Column(name = "maturity_period_months")
    private Integer maturityPeriodMonths;

    // 위험도 및 특성
    @Column(name = "risk_level", nullable = false, length = 20)
    private String riskLevel;

    @Column(name = "risk_score")
    private Integer riskScore;

    @Column(name = "volatility_level", length = 20)
    private String volatilityLevel;

    // 상품 특성
    @Column(name = "is_tax_free")
    private Boolean isTaxFree;

    @Column(name = "is_guaranteed")
    private Boolean isGuaranteed;

    @Column(name = "is_compound_interest")
    private Boolean isCompoundInterest;

    @Column(name = "is_monthly_interest")
    private Boolean isMonthlyInterest;

    // 상품 상태
    @Column(name = "product_status", length = 20)
    private String productStatus;

    @Column(name = "sales_start_date")
    private LocalDate salesStartDate;

    @Column(name = "sales_end_date")
    private LocalDate salesEndDate;

    // 상품 설명
    @Column(name = "product_description", columnDefinition = "TEXT")
    private String productDescription;

    @Column(name = "product_features", columnDefinition = "TEXT")
    private String productFeatures;

    @Column(name = "investment_strategy", columnDefinition = "TEXT")
    private String investmentStrategy;

    // 메타데이터
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "last_sync_at")
    private LocalDateTime lastSyncAt;
}
