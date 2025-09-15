package com.kopo_team4.kbbank_backend.domain.account.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountHolderNameRequest {
    
    /**
     * 은행 코드
     */
    private String bankCode;
    
    /**
     * 계좌 번호
     */
    private String accountNumber;
}
