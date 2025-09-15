package com.kopo_team4.kbbank_backend.domain.account.service;

import com.kopo_team4.kbbank_backend.domain.account.dto.AccountBalanceRequest;
import com.kopo_team4.kbbank_backend.domain.account.dto.AccountBalanceResponse;
import com.kopo_team4.kbbank_backend.domain.account.dto.AccountDetailRequest;
import com.kopo_team4.kbbank_backend.domain.account.dto.AccountDetailResponse;
import com.kopo_team4.kbbank_backend.domain.account.dto.AccountListResponse;
import com.kopo_team4.kbbank_backend.domain.account.dto.AccountMainResponse;
import com.kopo_team4.kbbank_backend.domain.account.dto.AccountResponse;
import com.kopo_team4.kbbank_backend.domain.account.dto.AccountSearchRequest;
import com.kopo_team4.kbbank_backend.domain.account.dto.AccountSearchByUserNumRequest;
import com.kopo_team4.kbbank_backend.domain.account.dto.AccountHolderNameRequest;
import com.kopo_team4.kbbank_backend.domain.account.dto.AccountHolderNameResponse;
import com.kopo_team4.kbbank_backend.domain.account.dto.AccountSubscriptionRequest;
import com.kopo_team4.kbbank_backend.domain.account.dto.AccountSubscriptionResponse;
import com.kopo_team4.kbbank_backend.domain.account.dto.AccountProfitInfoResponse;
import com.kopo_team4.kbbank_backend.domain.account.entity.Account;
import com.kopo_team4.kbbank_backend.domain.account.repository.AccountRepository;
import com.kopo_team4.kbbank_backend.domain.user.entity.User;
import com.kopo_team4.kbbank_backend.domain.user.repository.UserRepository;
import com.kopo_team4.kbbank_backend.domain.product.entity.Product;
import com.kopo_team4.kbbank_backend.domain.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class AccountService {
    
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    
    public AccountListResponse getAccountsByUserCi(String userCi) {
        // 1. CI로 사용자 조회
        User user = userRepository.findByUserCi(userCi)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
        
        // 2. 사용자 ID로 계좌 조회
        List<Account> accounts = accountRepository.findByUserId(user.getUserId());
        
        List<AccountResponse> accountResponses = accounts.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        
        // 목록 순번 설정
        for (int i = 0; i < accountResponses.size(); i++) {
            AccountResponse response = accountResponses.get(i);
            accountResponses.set(i, AccountResponse.builder()
                    .listNum(i + 1)
                    .bankCodeStd(response.getBankCodeStd())
                    .activityType(response.getActivityType())
                    .accountType(response.getAccountType())
                    .accountNum(response.getAccountNum())
                    .accountNumMasked(response.getAccountNumMasked())
                    .accountSeq(response.getAccountSeq())
                    .accountLocalCode(response.getAccountLocalCode())
                    .accountIssueDate(response.getAccountIssueDate())
                    .maturityDate(response.getMaturityDate())
                    .lastTranDate(response.getLastTranDate())
                    .productName(response.getProductName())
                    .productSubName(response.getProductSubName())
                    .dormancyYn(response.getDormancyYn())
                    .balanceAmt(response.getBalanceAmt())
                    .depositAmt(response.getDepositAmt())
                    .balanceCalcBasis1(response.getBalanceCalcBasis1())
                    .balanceCalcBasis2(response.getBalanceCalcBasis2())
                    .investmentLinkedYn(response.getInvestmentLinkedYn())
                    .bankLinkedYn(response.getBankLinkedYn())
                    .balanceAfterCancelYn(response.getBalanceAfterCancelYn())
                    .savingsBankCode(response.getSavingsBankCode())
                    .build());
        }
        
        return AccountListResponse.builder()
                .accounts(accountResponses)
                .totalCount(accountResponses.size())
                .build();
    }
    
    public AccountListResponse searchAccountsByUserCi(AccountSearchRequest request) {
        return getAccountsByUserCi(request.getUserCi());
    }
    
    public AccountListResponse searchAccountsByUserNum(AccountSearchByUserNumRequest request) {
        // 1. userNum으로 사용자 조회
        User user = userRepository.findByUserNum(request.getUserNum())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
        
        // 2. 사용자 ID로 계좌 조회
        List<Account> accounts = accountRepository.findByUserId(user.getUserId());
        
        List<AccountResponse> accountResponses = accounts.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        
        // 목록 순번 설정
        for (int i = 0; i < accountResponses.size(); i++) {
            AccountResponse response = accountResponses.get(i);
            accountResponses.set(i, AccountResponse.builder()
                    .listNum(i + 1)
                    .bankCodeStd(response.getBankCodeStd())
                    .activityType(response.getActivityType())
                    .accountType(response.getAccountType())
                    .accountNum(response.getAccountNum())
                    .accountNumMasked(response.getAccountNumMasked())
                    .accountSeq(response.getAccountSeq())
                    .accountLocalCode(response.getAccountLocalCode())
                    .accountIssueDate(response.getAccountIssueDate())
                    .maturityDate(response.getMaturityDate())
                    .lastTranDate(response.getLastTranDate())
                    .productName(response.getProductName())
                    .productSubName(response.getProductSubName())
                    .dormancyYn(response.getDormancyYn())
                    .balanceAmt(response.getBalanceAmt())
                    .depositAmt(response.getDepositAmt())
                    .balanceCalcBasis1(response.getBalanceCalcBasis1())
                    .balanceCalcBasis2(response.getBalanceCalcBasis2())
                    .investmentLinkedYn(response.getInvestmentLinkedYn())
                    .bankLinkedYn(response.getBankLinkedYn())
                    .balanceAfterCancelYn(response.getBalanceAfterCancelYn())
                    .savingsBankCode(response.getSavingsBankCode())
                    .build());
        }
        
        return AccountListResponse.builder()
                .accounts(accountResponses)
                .totalCount(accountResponses.size())
                .build();
    }
    
    /**
     * 사용자의 주거래 계좌를 추출하는 메서드
     * 우선순위:
     * 1. is_main_account가 true로 설정된 계좌
     * 2. 가장 최근에 거래한 계좌 (last_tran_date 기준)
     * 3. 계좌 유형이 당좌(2)인 계좌 (일반적으로 주거래용)
     * 4. 잔액이 가장 높은 계좌
     * 5. 가장 먼저 개설된 계좌
     */
    public AccountMainResponse getMainAccountByUserCi(String userCi) {
        // 1. CI로 사용자 조회
        User user = userRepository.findByUserCi(userCi)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
        
        // 2. 사용자 ID로 계좌 조회
        List<Account> accounts = accountRepository.findByUserId(user.getUserId());
        
        if (accounts.isEmpty()) {
            throw new IllegalArgumentException("해당 사용자의 계좌가 존재하지 않습니다.");
        }
        
        Account mainAccount = findMainAccount(accounts);
        return convertToMainResponse(mainAccount);
    }
    
    public AccountMainResponse searchMainAccountByUserCi(AccountSearchRequest request) {
        return getMainAccountByUserCi(request.getUserCi());
    }
    
    /**
     * 사용자 CI와 계좌번호로 특정 계좌의 상세정보를 조회하는 메서드
     */
    public AccountDetailResponse getAccountDetailByUserCi(AccountDetailRequest request) {
        // 1. CI로 사용자 조회
        User user = userRepository.findByUserCi(request.getUserCi())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
        
        // 2. 사용자 ID와 계좌번호로 특정 계좌 조회
        Account account = accountRepository.findByUserIdAndAccountNum(user.getUserId(), request.getAccountNum())
                .orElseThrow(() -> new IllegalArgumentException("해당 계좌가 존재하지 않습니다."));
        
        return convertToDetailResponse(account, user);
    }
    
    /**
     * 사용자 CI와 계좌번호로 계좌 잔액을 조회하는 메서드
     */
    public AccountBalanceResponse getAccountBalance(AccountBalanceRequest request) {
        // 1. CI로 사용자 조회
        User user = userRepository.findByUserCi(request.getUserCi())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
        
        // 2. 사용자 ID와 계좌번호로 특정 계좌 조회
        Account account = accountRepository.findByUserIdAndAccountNum(user.getUserId(), request.getAccountNum())
                .orElseThrow(() -> new IllegalArgumentException("해당 계좌가 존재하지 않습니다."));
        
        return convertToBalanceResponse(account);
    }
    
    private Account findMainAccount(List<Account> accounts) {

        // 1. 계좌 유형이 당좌(2)인 계좌 우선 (일반적으로 주거래용)
        Optional<Account> checkingAccount = accounts.stream()
                .filter(account -> "2".equals(account.getAccountType()))
                .findFirst();

        if (checkingAccount.isPresent()) {
            log.info("당좌계좌로 선택: {}", checkingAccount.get().getAccountNum());
            return checkingAccount.get();
        }

        // 2. 가장 최근에 거래한 계좌 (last_tran_date 기준)
        Optional<Account> recentAccount = accounts.stream()
                .filter(account -> account.getLastTranDate() != null)
                .max(Comparator.comparing(account -> parseDate(account.getLastTranDate())));
        
        if (recentAccount.isPresent()) {
            log.info("최근 거래일 기준으로 선택: {} (거래일: {})", 
                    recentAccount.get().getAccountNum(), 
                    recentAccount.get().getLastTranDate());
            return recentAccount.get();
        }
        
        // 3. 잔액이 가장 높은 계좌
        Optional<Account> highestBalanceAccount = accounts.stream()
                .filter(account -> account.getBalanceAmt() != null)
                .max(Comparator.comparing(Account::getBalanceAmt));
        
        if (highestBalanceAccount.isPresent()) {
            log.info("최고 잔액 기준으로 선택: {} (잔액: {})", 
                    highestBalanceAccount.get().getAccountNum(), 
                    highestBalanceAccount.get().getBalanceAmt());
            return highestBalanceAccount.get();
        }
        
        // 4. 가장 먼저 개설된 계좌
        Account oldestAccount = accounts.stream()
                .filter(account -> account.getAccountIssueDate() != null)
                .min(Comparator.comparing(account -> parseDate(account.getAccountIssueDate())))
                .orElse(accounts.get(0)); // 최후의 수단으로 첫 번째 계좌
        
        log.info("개설일 기준으로 선택: {} (개설일: {})", 
                oldestAccount.getAccountNum(), 
                oldestAccount.getAccountIssueDate());
        return oldestAccount;
    }
    
    private LocalDate parseDate(String dateString) {
        try {
            return LocalDate.parse(dateString, DateTimeFormatter.ofPattern("yyyyMMdd"));
        } catch (Exception e) {
            log.warn("날짜 파싱 실패: {}", dateString);
            return LocalDate.MIN; // 파싱 실패시 최소값 반환
        }
    }
    
    private AccountMainResponse convertToMainResponse(Account account) {
        return AccountMainResponse.builder()
                .accountNum(account.getAccountNum())
                .accountNumMasked(account.getAccountNumMasked())
                .productName(account.getProductName())
                .balanceAmt(account.getBalanceAmt())
                .bankCodeStd(account.getBankCodeStd())
                .accountType(account.getAccountType())
                .lastTranDate(account.getLastTranDate())
                .isMainAccount(false) // 기본값 false 설정
                .build();
    }
    
    private AccountResponse convertToResponse(Account account) {
        return AccountResponse.builder()
                .bankCodeStd(account.getBankCodeStd())
                .activityType(account.getActivityType())
                .accountType(account.getAccountType())
                .accountNum(account.getAccountNum())
                .accountNumMasked(account.getAccountNumMasked())
                .accountSeq(account.getAccountSeq())
                .accountLocalCode(account.getAccountLocalCode())
                .accountIssueDate(account.getAccountIssueDate())
                .maturityDate(account.getMaturityDate())
                .lastTranDate(account.getLastTranDate())
                .productName(account.getProductName())
                .productSubName(account.getProductSubName())
                .dormancyYn(account.getDormancyYn())
                .balanceAmt(account.getBalanceAmt())
                .depositAmt(account.getDepositAmt())
                .balanceCalcBasis1(account.getBalanceCalcBasis1())
                .balanceCalcBasis2(account.getBalanceCalcBasis2())
                .investmentLinkedYn(account.getInvestmentLinkedYn())
                .bankLinkedYn(account.getBankLinkedYn())
                .balanceAfterCancelYn(account.getBalanceAfterCancelYn())
                .savingsBankCode(account.getSavingsBankCode())
                .build();
    }

    private AccountDetailResponse convertToDetailResponse(Account account, User user) {
        // 은행 코드를 기반으로 은행명 매핑
        String bankName = mapBankCodeToName(account.getBankCodeStd());

        // 저축은행인 경우 개별저축은행명 설정
        String savingsBankName = "050".equals(account.getBankCodeStd()) ?
                mapSavingsBankCodeToName(account.getSavingsBankCode()) : null;

        return AccountDetailResponse.builder()
                .bankName(bankName)
                .savingsBankName(savingsBankName)
                .accountNum(account.getAccountNum())
                .accountSeq(account.getAccountSeq())
                .accountType(mapAccountType(account.getAccountType()))
                .scope("inquiry") // 기본값: 조회서비스
                .accountNumMasked(account.getAccountNumMasked())
                .payerNum(account.getAccountNum()) // 임시로 계좌번호 사용
                .inquiryAgreeYn("Y") // 기본값
                .transferAgreeYn("Y") // 기본값
                .userEmail(user.getEmail())
                .build();
    }
    
    private String mapBankCodeToName(String bankCode) {
        switch (bankCode) {
            case "088":
                return "신한은행";
            case "011":
                return "농협은행";
            case "003":
                return "기업은행";
            case "004":
                return "국민은행";
            case "020":
                return "우리은행";
            case "081":
                return "하나은행";
            case "050":
                return "저축은행";
            default:
                return "기타은행";
        }
    }
    
    private String mapSavingsBankCodeToName(String savingsBankCode) {
        if (savingsBankCode == null) {
            return null;
        }
        
        switch (savingsBankCode) {
            case "301":
                return "동양저축은행";
            case "302":
                return "웰컴저축은행";
            case "303":
                return "삼정저축은행";
            default:
                return "기타저축은행";
        }
    }
    
    private String mapAccountType(String accountType) {
        switch (accountType) {
            case "1":
                return "수시입출금";
            case "2":
                return "예적금";
            case "6":
                return "수익증권";
            case "T":
                return "종합계좌";
            default:
                return "기타";
        }
    }
    
    private String generateFintechUseNum(String accountNum) {
        // 핀테크 이용번호 생성 로직 (실제 구현에서는 고유 번호 생성 로직 필요)
        return "199999999" + accountNum.substring(0, Math.min(15, accountNum.length()));
    }
    
    private AccountBalanceResponse convertToBalanceResponse(Account account) {
        // 현재 날짜와 시간 생성
        String currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String bankTranId = "F" + UUID.randomUUID().toString().replace("-", "").substring(0, 20);
        
        // 은행 코드를 기반으로 은행명 매핑
        String bankName = mapBankCodeToName(account.getBankCodeStd());
        
        return AccountBalanceResponse.builder()
                .message("계좌 조회 정상처리")
                .bankTranId(bankTranId)
                .bankTranDate(currentDate)
                .bankCodeTran(account.getBankCodeStd())
                .bankRspCode("000")
                .bankRspMessage("정상 처리되었습니다.")
                .bankName(bankName)
                .accountNum(account.getAccountNum())
                .balanceAmt(account.getBalanceAmt() != null ? account.getBalanceAmt().toString() : "0")
                .availableAmt(account.getBalanceAmt() != null ? account.getBalanceAmt().toString() : "0")
                .accountType(account.getAccountType())
                .productName(account.getProductName())
                .accountIssueDate(account.getAccountIssueDate())
                .maturityDate(account.getMaturityDate())
                .lastTranDate(account.getLastTranDate())
                .build();
    }
    
    /**
     * 계좌주명 조회
     * 
     * 계좌번호와 은행코드로 Account와 User를 조인하여 계좌주명(username)을 조회합니다.
     * 
     * @param request 계좌주명 조회 요청 (은행코드, 계좌번호)
     * @return 계좌주명 응답
     * @throws IllegalArgumentException 존재하지 않는 계좌인 경우
     */
    public AccountHolderNameResponse getAccountHolderName(AccountHolderNameRequest request) {
        log.info("계좌주명 조회 시작 - 은행코드: {}, 계좌번호: {}", request.getBankCode(), request.getAccountNumber());
        
        try {
            // 1. 먼저 계좌가 존재하는지 확인
            Optional<Account> accountOpt = accountRepository.findByAccountNum(request.getAccountNumber());
            if (accountOpt.isEmpty()) {
                log.warn("계좌를 찾을 수 없음 - 계좌번호: {}", request.getAccountNumber());
                throw new IllegalArgumentException("존재하지 않는 계좌입니다.");
            }
            
            Account account = accountOpt.get();
            
            // 2. 은행코드 검증
            if (!request.getBankCode().equals(account.getBankCodeStd())) {
                log.warn("은행코드 불일치 - 요청: {}, 실제: {}", request.getBankCode(), account.getBankCodeStd());
                throw new IllegalArgumentException("존재하지 않는 계좌입니다.");
            }
            
            // 3. userId로 User 조회하여 username 획득
            Optional<User> userOpt =
                    userRepository.findById(account.getUserId());
            if (userOpt.isEmpty()) {
                log.warn("사용자를 찾을 수 없음 - userId: {}", account.getUserId());
                throw new IllegalArgumentException("계좌주명을 확인할 수 없습니다.");
            }
            
            User user = userOpt.get();
            String username = user.getUsername();
            
            if (username == null || username.trim().isEmpty()) {
                log.warn("계좌주명이 비어있음 - userId: {}", account.getUserId());
                throw new IllegalArgumentException("계좌주명을 확인할 수 없습니다.");
            }
            
            log.info("계좌주명 조회 성공 - 계좌주명: {}", username);
            
            return AccountHolderNameResponse.builder()
                    .accountHolderName(username.trim())
                    .build();
                    
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            log.error("계좌주명 조회 실패 - 은행코드: {}, 계좌번호: {}, 오류: {}", 
                     request.getBankCode(), request.getAccountNumber(), e.getMessage(), e);
            throw new RuntimeException("계좌주명 조회 중 오류가 발생했습니다.", e);
        }
    }

    /**
     * 상품 가입 및 계좌 생성
     */
    @Transactional
    public AccountSubscriptionResponse subscribeProduct(AccountSubscriptionRequest request) {
        log.info("상품 가입 시작 - 상품ID: {}, 사용자: {}, 금액: {}", 
                request.getProductId(), request.getUserCi(), request.getAmount());
        
        // 1. 사용자 조회
        User user = userRepository.findByUserCi(request.getUserCi())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
        
        // 2. 상품 조회
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품입니다."));
        
        // 3. 계좌번호 생성 (중복 체크 포함)
        String accountNumber = generateUniqueAccountNumber();
        
        // 4. 계좌 생성
        Account account = Account.builder()
                .accountNum(accountNumber)
                .userId(user.getUserId())
                .bankCodeStd("081") // 하나은행
                .activityType("1")  // 활성
                .accountType(getAccountTypeByProductType(product.getProductType()))
                .accountNumMasked(maskAccountNumber(accountNumber))
                .accountSeq("01")
                .accountLocalCode("0000001")
                .accountIssueDate(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")))
                .maturityDate(request.getPeriodMonths() != null ? 
                    LocalDate.now().plusMonths(request.getPeriodMonths()).format(DateTimeFormatter.ofPattern("yyyyMMdd")) : null)
                .lastTranDate(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")))
                .productName(product.getProductName())
                .productSubName(product.getProductSubName())
                .dormancyYn("N")
                .balanceAmt(BigDecimal.ZERO) // 초기 잔액은 0원으로 설정
                .depositAmt("SAVINGS".equals(product.getProductType()) ? request.getAmount() : request.getAmount())
                .productId(product.getProductId())
                .sourceAccountNumber(request.getSourceAccountNumber()) // 출금계좌번호 저장
                .build();
        
        Account savedAccount = accountRepository.save(account);
        
        log.info("계좌 생성 완료 - 계좌번호: {}, 상품명: {}", accountNumber, product.getProductName());
        
        // 5. 응답 생성
        return AccountSubscriptionResponse.builder()
                .accountNumber(accountNumber)
                .subscriptionId("SUB" + System.currentTimeMillis())
                .productName(product.getProductName())
                .productType(product.getProductType())
                .amount(request.getAmount())
                .monthlyAmount(request.getMonthlyAmount())
                .contractDate(LocalDate.now())
                .maturityDate(request.getPeriodMonths() != null ? 
                    LocalDate.now().plusMonths(request.getPeriodMonths()) : null)
                .interestRate("SAVINGS".equals(product.getProductType()) ? product.getBaseRate() : null)
                .returnRate("FUND".equals(product.getProductType()) ? product.getExpectedReturnRate() : null)
                .status("ACTIVE")
                .build();
    }
    
    /**
     * 계좌별 수익 정보 조회
     */
    @Transactional
    public AccountProfitInfoResponse getAccountProfitInfo(String accountNumber, String userCi) {
        log.info("계좌 수익 정보 조회 시작 - 계좌번호: {}, 사용자: {}", accountNumber, userCi);
        
        // 1. 계좌 조회
        Account account = accountRepository.findByAccountNum(accountNumber)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 계좌입니다."));
        
        // 2. 사용자 검증
        User user = userRepository.findByUserCi(userCi)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
        
        if (!account.getUserId().equals(user.getUserId())) {
            throw new IllegalArgumentException("해당 계좌에 대한 권한이 없습니다.");
        }
        
        // 3. 상품 정보 조회
        Product product = productRepository.findByProductName(account.getProductName())
                .orElse(null);
        
        log.info("수익 정보 조회 완료 - 계좌번호: {}, 수익: {}, 수익률: {}", 
                accountNumber, account.getCurrentProfit(), account.getCurrentReturnRate());
        
        // DB에 저장된 수익금 사용 (자동 계산 없음)
        BigDecimal currentProfit = account.getCurrentProfit() != null ? account.getCurrentProfit() : BigDecimal.ZERO;
        
        // 상품 타입에 따라 응답 데이터 설정
        BigDecimal currentBalance;
        BigDecimal totalDeposit;
        BigDecimal displayBaseRate = null;
        BigDecimal displayProfitRate = null;
        BigDecimal displayInterestRate = null;
        LocalDateTime lastUpdated = account.getLastProfitCalculated() != null ? account.getLastProfitCalculated() : LocalDateTime.now();
        
        if (product != null && "FUND".equals(product.getProductType())) {
            // 펀드의 경우 - 수익률 계산 및 DB 업데이트
            log.info("펀드 상품 수익률 계산 시작 - 계좌번호: {}", accountNumber);
            
            BigDecimal balanceAmt = account.getBalanceAmt() != null ? account.getBalanceAmt() : BigDecimal.ZERO;
            totalDeposit = balanceAmt; // 원금 (잔액)
            currentBalance = balanceAmt.add(currentProfit); // 잔액 + 수익금
            
            // 수익률 계산: (수익금 / 원금) × 100
            BigDecimal calculatedReturnRate = BigDecimal.ZERO;
            if (totalDeposit.compareTo(BigDecimal.ZERO) > 0) {
                calculatedReturnRate = currentProfit.multiply(BigDecimal.valueOf(100))
                    .divide(totalDeposit, 2, RoundingMode.HALF_UP);
            }
            
            // DB에 수익률 업데이트
            LocalDateTime now = LocalDateTime.now();
            int updateResult = accountRepository.updateAccountReturnRate(
                account.getAccountId(), calculatedReturnRate, now);
            
            if (updateResult > 0) {
                log.info("펀드 수익률 DB 업데이트 성공 - 계좌번호: {}, 수익률: {}%", 
                    accountNumber, calculatedReturnRate);
                lastUpdated = now;
            } else {
                log.warn("펀드 수익률 DB 업데이트 실패 - 계좌번호: {}", accountNumber);
            }
            
            displayProfitRate = calculatedReturnRate;
            displayInterestRate = null; // 펀드는 이자율 없음
            
        } else if (product != null && "SAVINGS".equals(product.getProductType())) {
            // 적금의 경우
            currentBalance = account.getBalanceAmt() != null ? account.getBalanceAmt() : BigDecimal.ZERO;
            totalDeposit = currentBalance; // balance_amt를 납입금으로 사용
            
            // 디버깅 로그 추가
            log.info("적금 상품 - 계좌번호: {}, balanceAmt: {}, depositAmt: {}, currentBalance: {}, totalDeposit: {}", 
                    accountNumber, account.getBalanceAmt(), account.getDepositAmt(), currentBalance, totalDeposit);
            
            // baseRate: 고정 금리 사용 (DB의 base_rate 필드)
            displayBaseRate = product.getBaseRate() != null ? product.getBaseRate() : BigDecimal.ZERO;
            displayInterestRate = product.getBaseRate() != null ? product.getBaseRate() : BigDecimal.ZERO;
            
        } else {
            // 기타 상품
            currentBalance = account.getBalanceAmt() != null ? account.getBalanceAmt() : BigDecimal.ZERO;
            totalDeposit = currentBalance; // balance_amt를 납입금으로 사용
            
            // 디버깅 로그 추가
            log.info("기타 상품 - 계좌번호: {}, balanceAmt: {}, depositAmt: {}, currentBalance: {}, totalDeposit: {}", 
                    accountNumber, account.getBalanceAmt(), account.getDepositAmt(), currentBalance, totalDeposit);
            
            displayInterestRate = product != null && product.getBaseRate() != null ? 
                product.getBaseRate() : BigDecimal.ZERO;
        }

        return AccountProfitInfoResponse.builder()
                .accountNumber(accountNumber)
                .productType(product != null ? product.getProductType() : "UNKNOWN")
                .currentBalance(currentBalance)
                .totalDeposit(totalDeposit)
                .baseRate(displayBaseRate)
                .profitRate(displayProfitRate)
                .interestRate(displayInterestRate)
                .profit(currentProfit)
                .lastUpdated(lastUpdated)
                .build();
    }
    

    
    /**
     * 상품 타입별 계좌 타입 매핑
     */
    private String getAccountTypeByProductType(String productType) {
        switch (productType) {
            case "SAVINGS":
            case "DEPOSIT":
                return "2"; // 예적금
            case "FUND":
            case "BOND":
            case "ETF":
                return "6"; // 수익증권
            default:
                return "1"; // 수시입출금
        }
    }
    
    /**
     * 고유한 계좌번호 생성 (중복 체크 포함)
     * 형식: 110-1234567890123 (3-13자리, 총 17자리)
     */
    private String generateUniqueAccountNumber() {
        String accountNumber;
        int maxAttempts = 10;
        int attempts = 0;
        
        do {
            String baseNumber = "110" + String.format("%013d", System.currentTimeMillis() % 10000000000000L);
            accountNumber = baseNumber.substring(0, 3) + "-" + baseNumber.substring(3);
            attempts++;
            
            if (attempts >= maxAttempts) {
                throw new RuntimeException("계좌번호 생성 중 중복이 계속 발생합니다.");
            }
        } while (accountRepository.existsByAccountNum(accountNumber));
        
        return accountNumber;
    }
    
    /**
     * 계좌번호 마스킹
     * 형식: 110-1234567890123 → 110-****90123
     */
    private String maskAccountNumber(String accountNumber) {
        if (accountNumber == null || accountNumber.length() < 9) {
            return accountNumber;
        }
        
        // 하이픈이 포함된 계좌번호 처리: 110-1234567890123 → 110-****90123
        if (accountNumber.contains("-")) {
            String[] parts = accountNumber.split("-");
            if (parts.length == 2 && parts[1].length() >= 8) {
                String bankCode = parts[0]; // 110
                String accountPart = parts[1]; // 1234567890123
                String maskedPart = "****" + accountPart.substring(accountPart.length() - 5);
                return bankCode + "-" + maskedPart;
            }
        }
        
        // 하이픈이 없는 기존 형식 처리 (하위 호환성)
        return accountNumber.substring(0, 4) + "****" + accountNumber.substring(accountNumber.length() - 4);
    }
} 