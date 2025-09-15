package com.kopo_team4.kbbank_backend.domain.account.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountBalanceResponse {
    
    // 응답메세지
    @JsonProperty("message")
    private String message;
    
    // 정보제공자 내부에서 발생한 API transaction 내역
    @JsonProperty("bankTranId")
    private String bankTranId;
    
    @JsonProperty("bankTranDate")
    private String bankTranDate;
    
    @JsonProperty("bankCodeTran")
    private String bankCodeTran;
    
    @JsonProperty("bankRspCode")
    private String bankRspCode;
    
    @JsonProperty("bankRspMessage")
    private String bankRspMessage;
    
    // 계좌정보
    @JsonProperty("bankName")
    private String bankName;
    
    @JsonProperty("accountNum")
    private String accountNum;
    
    @JsonProperty("balanceAmt")
    private String balanceAmt;
    
    @JsonProperty("availableAmt")
    private String availableAmt;
    
    @JsonProperty("accountType")
    private String accountType;
    
    @JsonProperty("productName")
    private String productName;
    
    @JsonProperty("accountIssueDate")
    private String accountIssueDate;
    
    @JsonProperty("maturityDate")
    private String maturityDate;
    
    @JsonProperty("lastTranDate")
    private String lastTranDate;
} 