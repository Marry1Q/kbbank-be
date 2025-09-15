package com.kopo_team4.kbbank_backend.domain.account.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountDetailResponse {

    private String bankName;

    private String savingsBankName;

    private String accountNum;

    private String accountSeq;

    private String accountType;

    private String scope;

    private String accountNumMasked;

    private String payerNum;

    private String inquiryAgreeYn;

    private String transferAgreeYn;

    private String userEmail;
}