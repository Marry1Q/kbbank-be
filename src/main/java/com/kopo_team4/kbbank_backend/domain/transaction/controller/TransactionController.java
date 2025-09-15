package com.kopo_team4.kbbank_backend.domain.transaction.controller;

import com.kopo_team4.kbbank_backend.domain.transaction.dto.*;
import com.kopo_team4.kbbank_backend.domain.transaction.dto.*;
import com.kopo_team4.kbbank_backend.domain.transaction.service.TransactionService;
import com.kopo_team4.kbbank_backend.global.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
@Slf4j
public class TransactionController {

    private final TransactionService transactionService;

    /**
     * 출금 API
     * POST /api/v1/transactions/withdraw
     */
    @PostMapping("/withdraw")
    public ResponseEntity<ApiResponse<WithdrawResponse>> withdraw(@RequestBody WithdrawRequest request) {
        log.info("=====================================================");
        log.info("🏧 [HANA-BACKEND] 출금 요청 수신");
        log.info("=====================================================");
        log.info("📤 요청 데이터:");
        log.info("  ├─ userCI: {}", request.getUserCI());
        log.info("  ├─ 출금계좌번호: {}", request.getWdAccountNum());
        log.info("  ├─ 거래금액: {}", request.getTranAmt());
        log.info("  ├─ 은행거래ID: {}", request.getBankTranId());
        log.info("  ├─ 거래시간: {}", request.getTranDtime());
        log.info("  └─ 요청자명: {}", request.getReqClientName());
        log.info("⏰ 요청 시간: {}", java.time.LocalDateTime.now());
        log.info("=====================================================");

        try {
            WithdrawResponse response = transactionService.processWithdraw(request);
            
            log.info("=====================================================");
            log.info("✅ [HANA-BACKEND] 출금 처리 성공");
            log.info("=====================================================");
            log.info("📥 응답 데이터:");
            log.info("  ├─ 은행거래ID: {}", response.getBankTranId());
            log.info("  ├─ 계좌번호: {}", response.getAccountNum());
            log.info("  ├─ 거래금액: {}", response.getTranAmt());
            log.info("  ├─ 은행명: {}", response.getBankName());
            log.info("  └─ 예금주명: {}", response.getAccountHolderName());
            log.info("⏰ 응답 시간: {}", java.time.LocalDateTime.now());
            log.info("=====================================================");
            
            return ResponseEntity.ok(ApiResponse.success(response));

        } catch (IllegalArgumentException e) {
            log.warn("출금 실패 - 잘못된 요청: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));

        } catch (IllegalStateException e) {
            log.error("출금 실패 - 시스템 오류: {}", e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("출금 처리 중 오류가 발생했습니다."));

        } catch (Exception e) {
            log.error("출금 처리 중 예상치 못한 오류 발생", e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("출금 처리 중 오류가 발생했습니다."));
        }
    }

    /**
     * 입금 API
     * POST /api/v1/transactions/deposit
     */
    @PostMapping("/deposit")
    public ResponseEntity<ApiResponse<DepositResponse>> deposit(@RequestBody DepositRequest request) {
        log.info("=====================================================");
        log.info("🏦 [HANA-BACKEND] 입금 요청 수신");
        log.info("=====================================================");
        log.info("📤 요청 데이터:");
        log.info("  ├─ 입금계좌번호: {}", request.getAccountNum());
        log.info("  ├─ 거래금액: {}", request.getTranAmt());
        log.info("  ├─ 은행거래ID: {}", request.getBankTranId());
        log.info("  ├─ 거래시간: {}", request.getTranDtime());
        log.info("  ├─ 예금주명: {}", request.getAccountHolderName());
        log.info("  └─ 요청고객번호: {}", request.getReqClientNum());
        log.info("⏰ 요청 시간: {}", java.time.LocalDateTime.now());
        log.info("=====================================================");

        try {
            DepositResponse response = transactionService.processDeposit(request);
            
            log.info("=====================================================");
            log.info("✅ [HANA-BACKEND] 입금 처리 성공");
            log.info("=====================================================");
            log.info("📥 응답 데이터:");
            log.info("  ├─ 은행거래ID: {}", response.getBankTranId());
            log.info("  ├─ 계좌번호: {}", response.getAccountNum());
            log.info("  ├─ 거래금액: {}", response.getTranAmt());
            log.info("  ├─ 은행명: {}", response.getBankName());
            log.info("  ├─ 예금주명: {}", response.getAccountHolderName());
            log.info("  └─ 출금은행거래ID: {}", response.getWithdrawBankTranId());
            log.info("⏰ 응답 시간: {}", java.time.LocalDateTime.now());
            log.info("=====================================================");
            
            return ResponseEntity.ok(ApiResponse.success(response));
            
        } catch (IllegalArgumentException e) {
            log.warn("입금 실패 - 잘못된 요청: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
                    
        } catch (IllegalStateException e) {
            log.error("입금 실패 - 시스템 오류: {}", e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("입금 처리 중 오류가 발생했습니다."));
                    
        } catch (Exception e) {
            log.error("입금 처리 중 예상치 못한 오류 발생", e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("입금 처리 중 오류가 발생했습니다."));
        }
    }

    // TransactionController.java에 추가할 메서드
    /**
     * 거래내역 조회 API
         * POST /api/v1/transactions/history
     */
    @PostMapping("/history")
    public ResponseEntity<ApiResponse<TransactionHistoryResponse>> getTransactionHistory(
            @Valid @RequestBody TransactionHistoryRequest request) {
        log.info("거래내역 조회 요청 - CI: {}, 계좌번호: {}, 조회타입: {}, 정렬: {}",
                request.getUserCi(), request.getAccountNum(), request.getInquiryType(), request.getSortOrder());

        try {
            TransactionHistoryResponse response = transactionService.getTransactionHistory(request);
            log.info("거래내역 조회 성공 - 거래건수: {}", response.getPageRecordCnt());
            return ResponseEntity.ok(ApiResponse.success(response));

        } catch (IllegalArgumentException e) {
            log.warn("거래내역 조회 실패: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));

        } catch (Exception e) {
            log.error("거래내역 조회 중 오류 발생", e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("거래내역 조회 중 오류가 발생했습니다."));
        }
    }
} 