package com.kopo_team4.kbbank_backend.domain.account.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountHolderNameResponse {
    
    /**
     * 계좌주명 (예금주명)
     */
    private String accountHolderName;
}
