package com.kopo_team4.kbbank_backend.domain.account.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountDetailRequest {
    
    private String userCi;
    
    private String accountNum;
} 