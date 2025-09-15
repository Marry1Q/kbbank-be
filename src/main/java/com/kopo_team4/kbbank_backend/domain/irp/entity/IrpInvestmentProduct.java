package com.kopo_team4.kbbank_backend.domain.irp.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "KBBANK_IRP_INVESTMENT_PRODUCT")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IrpInvestmentProduct{
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PRODUCT_ID")
    private Long productId;
    
    @Column(name = "IRP_ID", nullable = false)
    private Long irpId;
    
    @Column(name = "PRODUCT_NAME", length = 100, nullable = false)
    private String productName;
    
    @Column(name = "BANK_CODE_STD", length = 3, nullable = false)
    private String bankCodeStd;
    
    @Column(name = "INVEST_AMT", precision = 15, scale = 2)
    private BigDecimal investAmt;
    
    @Column(name = "EXPECTED_YIELD", precision = 5, scale = 2)
    private BigDecimal expectedYield;
    
    @Column(name = "ACTUAL_YIELD", precision = 5, scale = 2)
    private BigDecimal actualYield;
    
    @Column(name = "START_DATE")
    private LocalDate startDate;
    
    @Column(name = "MATURITY_DATE")
    private LocalDate maturityDate;
    
    @CreationTimestamp
    @Column(name = "CREATED_AT", updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt;
} 