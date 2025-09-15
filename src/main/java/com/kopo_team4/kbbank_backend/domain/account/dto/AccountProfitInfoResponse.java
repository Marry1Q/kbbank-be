package com.kopo_team4.kbbank_backend.domain.account.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountProfitInfoResponse {
    
    private String accountNumber;
    
    private String productType;
    
    private BigDecimal currentBalance;
    
    private BigDecimal totalDeposit;
    
    private BigDecimal baseRate;
    
    private BigDecimal profitRate;
    
    private BigDecimal interestRate;
    
    private BigDecimal profit;
    
    private LocalDateTime lastUpdated;
}
