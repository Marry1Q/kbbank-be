package com.kopo_team4.kbbank_backend.domain.account.controller;

import com.kopo_team4.kbbank_backend.domain.account.dto.AccountBalanceRequest;
import com.kopo_team4.kbbank_backend.domain.account.dto.AccountBalanceResponse;
import com.kopo_team4.kbbank_backend.domain.account.dto.AccountDetailRequest;
import com.kopo_team4.kbbank_backend.domain.account.dto.AccountDetailResponse;
import com.kopo_team4.kbbank_backend.domain.account.dto.AccountListResponse;
import com.kopo_team4.kbbank_backend.domain.account.dto.AccountMainResponse;
import com.kopo_team4.kbbank_backend.domain.account.dto.AccountSearchRequest;
import com.kopo_team4.kbbank_backend.domain.account.dto.AccountSearchByUserNumRequest;
import com.kopo_team4.kbbank_backend.domain.account.dto.AccountHolderNameRequest;
import com.kopo_team4.kbbank_backend.domain.account.dto.AccountHolderNameResponse;
import com.kopo_team4.kbbank_backend.domain.account.dto.AccountSubscriptionRequest;
import com.kopo_team4.kbbank_backend.domain.account.dto.AccountSubscriptionResponse;
import com.kopo_team4.kbbank_backend.domain.account.dto.AccountProfitInfoResponse;
import com.kopo_team4.kbbank_backend.domain.account.service.AccountService;
import com.kopo_team4.kbbank_backend.global.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
@Slf4j
public class AccountController {
    
    private final AccountService accountService;
    
    /**
     * 사용자 CI로 계좌 목록 조회 API (POST 방식)
     * POST /api/v1/accounts/search
     */
    @PostMapping("/search")
    public ResponseEntity<ApiResponse<AccountListResponse>> searchAccountsByUserCi(@RequestBody AccountSearchRequest request) {
        log.info("계좌 목록 조회 요청 (POST) - CI: {}", request.getUserCi());
        try {
            AccountListResponse response = accountService.searchAccountsByUserCi(request);
            log.info("계좌 목록 조회 성공 - 계좌 수: {}", response.getTotalCount());
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (IllegalArgumentException e) {
            log.warn("계좌 목록 조회 실패 - 존재하지 않는 사용자: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("계좌 목록 조회 중 오류 발생", e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("계좌 목록 조회 중 오류가 발생했습니다."));
        }
    }
    
    /**
     * 사용자 번호로 계좌 목록 조회 API (POST 방식)
     * POST /api/v1/accounts/search-by-user-num
     */
    @PostMapping("/search-by-user-num")
    public ResponseEntity<ApiResponse<AccountListResponse>> searchAccountsByUserNum(@Valid @RequestBody AccountSearchByUserNumRequest request) {
        log.info("계좌 목록 조회 요청 (POST) - UserNum: {}", request.getUserNum());
        try {
            AccountListResponse response = accountService.searchAccountsByUserNum(request);
            log.info("계좌 목록 조회 성공 - 계좌 수: {}", response.getTotalCount());
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (IllegalArgumentException e) {
            log.warn("계좌 목록 조회 실패 - 존재하지 않는 사용자: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("계좌 목록 조회 중 오류 발생", e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("계좌 목록 조회 중 오류가 발생했습니다."));
        }
    }
    
    /**
     * 사용자 CI로 주거래 계좌 조회 API (POST 방식 - 보안 강화)
     * POST /api/v1/accounts/main
     */
    @PostMapping("/main")
    public ResponseEntity<ApiResponse<AccountMainResponse>> searchMainAccountByUserCi(@RequestBody AccountSearchRequest request) {
        log.info("주거래 계좌 조회 요청 (POST) - CI: {}", request.getUserCi());
        try {
            AccountMainResponse response = accountService.searchMainAccountByUserCi(request);
            log.info("주거래 계좌 조회 성공 - 계좌번호: {}", response.getAccountNumMasked());
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (IllegalArgumentException e) {
            log.warn("주거래 계좌 조회 실패 - 존재하지 않는 사용자: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("주거래 계좌 조회 중 오류 발생", e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("주거래 계좌 조회 중 오류가 발생했습니다."));
        }
    }
    
    /**
     * 사용자 CI와 계좌번호로 계좌 상세 정보 조회 API
     * POST /api/v1/accounts/detail
     */
    @PostMapping("/detail")
    public ResponseEntity<ApiResponse<AccountDetailResponse>> getAccountDetailByUserCi(@RequestBody AccountDetailRequest request) {
        log.info("계좌 상세 정보 조회 요청 - CI: {}, 계좌번호: {}", request.getUserCi(), request.getAccountNum());
        try {
            AccountDetailResponse response = accountService.getAccountDetailByUserCi(request);
            log.info("계좌 상세 정보 조회 성공 - 계좌번호: {}", response.getAccountNumMasked());
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (IllegalArgumentException e) {
            log.warn("계좌 상세 정보 조회 실패: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("계좌 상세 정보 조회 중 오류 발생", e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("계좌 상세 정보 조회 중 오류가 발생했습니다."));
        }
    }
    
    /**
     * 사용자 CI와 계좌번호로 계좌 잔액 조회 API
     * POST /api/v1/accounts/balance
     */
    @PostMapping("/balance")
    public ResponseEntity<ApiResponse<AccountBalanceResponse>> getAccountBalance(@Valid @RequestBody AccountBalanceRequest request) {
        log.info("계좌 잔액 조회 요청 - CI: {}, 계좌번호: {}", request.getUserCi(), request.getAccountNum());
        try {
            AccountBalanceResponse response = accountService.getAccountBalance(request);
            log.info("계좌 잔액 조회 성공 - 계좌번호: {}, 잔액: {}", response.getAccountNum(), response.getBalanceAmt());
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (IllegalArgumentException e) {
            log.warn("계좌 잔액 조회 실패: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("계좌 잔액 조회 중 오류 발생", e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("계좌 잔액 조회 중 오류가 발생했습니다."));
        }
    }
    
    /**
     * 계좌주명 조회 API (POST 방식)
     * POST /api/v1/accounts/holder-name
     */
    @PostMapping("/holder-name")
    public ResponseEntity<ApiResponse<AccountHolderNameResponse>> getAccountHolderName(@RequestBody AccountHolderNameRequest request) {
        log.info("계좌주명 조회 요청 - 은행코드: {}, 계좌번호: {}", request.getBankCode(), request.getAccountNumber());
        try {
            AccountHolderNameResponse response = accountService.getAccountHolderName(request);
            log.info("계좌주명 조회 성공 - 계좌주명: {}", response.getAccountHolderName());
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (IllegalArgumentException e) {
            log.warn("계좌주명 조회 실패 - 존재하지 않는 계좌: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("계좌주명 조회 중 오류 발생", e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("계좌주명 조회 중 오류가 발생했습니다."));
        }
    }
    
    /**
     * 상품 가입 및 계좌 생성 API
     * POST /api/v1/accounts/subscribe
     */
    @PostMapping("/subscribe")
    public ResponseEntity<ApiResponse<AccountSubscriptionResponse>> subscribeProduct(
            @Valid @RequestBody AccountSubscriptionRequest request) {
        
        log.info("상품 가입 요청 - 상품ID: {}, 사용자: {}, 금액: {}", 
                request.getProductId(), request.getUserCi(), request.getAmount());
        
        try {
            AccountSubscriptionResponse response = accountService.subscribeProduct(request);
            log.info("상품 가입 성공 - 계좌번호: {}", response.getAccountNumber());
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (IllegalArgumentException e) {
            log.warn("상품 가입 실패 - 잘못된 요청: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("상품 가입 중 오류 발생", e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("상품 가입 중 오류가 발생했습니다."));
        }
    }
    
    /**
     * 계좌별 수익 정보 조회 API
     * GET /api/v1/accounts/{accountNumber}/profit-info
     */
    @GetMapping("/{accountNumber}/profit-info")
    public ResponseEntity<ApiResponse<AccountProfitInfoResponse>> getAccountProfitInfo(
            @PathVariable String accountNumber,
            @RequestParam String userCi) {
        
        log.info("계좌 수익 정보 조회 - 계좌번호: {}, 사용자: {}", accountNumber, userCi);
        
        try {
            AccountProfitInfoResponse response = accountService.getAccountProfitInfo(accountNumber, userCi);
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (IllegalArgumentException e) {
            log.warn("계좌 수익 정보 조회 실패 - 잘못된 요청: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("계좌 수익 정보 조회 중 오류 발생", e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("계좌 수익 정보 조회 중 오류가 발생했습니다."));
        }
    }
} 