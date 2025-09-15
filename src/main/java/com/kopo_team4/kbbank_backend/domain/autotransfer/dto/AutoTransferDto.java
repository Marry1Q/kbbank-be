package com.kopo_team4.kbbank_backend.domain.autotransfer.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class AutoTransferDto {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "자동이체 등록 요청")
    public static class CreateRequest {
        @NotBlank
        @Schema(description = "사용자 CI")
        private String userCi;

        @NotBlank
        @Schema(description = "출금 계좌번호")
        private String fromAccountNumber;

        @NotBlank
        @Schema(description = "이체 대상 계좌번호")
        private String toAccountNumber;

        @NotBlank
        @Schema(description = "이체 대상 계좌주명")
        private String toAccountName;

        @NotBlank
        @Schema(description = "이체 대상 은행코드")
        private String toBankCode;

        @NotNull
        @Positive
        @Schema(description = "이체 금액")
        private BigDecimal amount;

        @NotBlank
        @Schema(description = "이체 주기 (예: 매월 25일)")
        private String schedule;

        @Schema(description = "다음 이체 예정일 (미전송 시 서버 계산)")
        private LocalDate nextTransferDate;

        @Schema(description = "메모")
        private String memo;
        
        // 새로 추가되는 필드들
        @Schema(description = "자동이체 상태 (ACTIVE/INACTIVE)")
        private String status;
        
        @Schema(description = "마지막 실행 상태 (SUCCESS/FAILED/PENDING)")
        private String lastExecutionStatus;
        
        @NotNull(message = "총 납입 기간은 필수입니다.")
        @Positive(message = "총 납입 기간은 0보다 커야 합니다.")
        @Schema(description = "총 납입 기간 (개월)", example = "12")
        private Integer periodMonths;
        
        @Schema(description = "초기 상태", example = "SUCCESS", allowableValues = {"SUCCESS", "FAILED", "PENDING"})
        private String initialStatus;
        
        @Schema(description = "현재 회차", example = "1")
        private Integer currentInstallment;
        
        @Schema(description = "남은 회차", example = "11")
        private Integer remainingInstallments;
        
        @Schema(description = "마지막 실행일", example = "2024-01-25")
        private LocalDate lastExecutionDate;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "자동이체 수정 요청")
    public static class UpdateRequest {
        @NotBlank
        private String toAccountNumber;
        @NotBlank
        private String toAccountName;
        @NotBlank
        private String toBankCode;
        @NotNull
        @Positive
        private BigDecimal amount;
        @NotBlank
        private String schedule;
        private LocalDate nextTransferDate;
        private String memo;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "자동이체 목록 조회 요청")
    public static class SearchRequest {
        @NotBlank
        @Schema(description = "출금 계좌번호")
        private String fromAccountNumber;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "자동이체 응답")
    public static class Response {
        private Long autoTransferId;
        private String userId;
        private String userCi;  // userCi 필드 추가
        private Long fromAccountId;
        private String toAccountNumber;
        private String toAccountName;
        private String toBankCode;
        private BigDecimal amount;
        private String schedule;
        private LocalDate nextTransferDate;
        private String memo;
        private String status;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        
        // 새로 추가되는 필드
        private String lastExecutionStatus;
        
        // 회차 관련 필드들 추가
        private Integer totalInstallments;
        private Integer currentInstallment;
        private Integer remainingInstallments;
        private LocalDate lastExecutionDate;
    }
}
