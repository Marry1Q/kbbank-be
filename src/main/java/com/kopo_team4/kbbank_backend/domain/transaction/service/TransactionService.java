package com.kopo_team4.kbbank_backend.domain.transaction.service;

import com.kopo_team4.kbbank_backend.domain.account.entity.Account;
import com.kopo_team4.kbbank_backend.domain.account.repository.AccountRepository;
import com.kopo_team4.kbbank_backend.domain.transaction.dto.*;
import com.kopo_team4.kbbank_backend.domain.transaction.dto.*;
import com.kopo_team4.kbbank_backend.domain.transaction.entity.Transaction;
import com.kopo_team4.kbbank_backend.domain.transaction.repository.TransactionRepository;
import com.kopo_team4.kbbank_backend.domain.user.entity.User;
import com.kopo_team4.kbbank_backend.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TransactionService {
    
    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    
    /**
     * 출금 처리
     */
    public WithdrawResponse processWithdraw(WithdrawRequest request) {
        log.info("💰 [HANA-BACKEND] 출금 처리 시작");
        
        // 0. 출금 금액 검증
        if (request.getTranAmt() == null || request.getTranAmt().compareTo(BigDecimal.ZERO) <= 0) {
            log.error("❌ 출금 금액 검증 실패: {}", request.getTranAmt());
            throw new IllegalArgumentException("출금 금액은 0원보다 커야 합니다.");
        }
        log.info("✅ 출금 금액 검증 통과: {:,}원", request.getTranAmt().intValue());
        
        // 1. 사용자 조회 (userCI로 조회)
        User user = userRepository.findByUserCi(request.getUserCI())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
        log.info("✅ 사용자 조회 성공: userId={}", user.getUserId());
        
        // 2. 계좌 조회 (사용자의 계좌인지 확인)
        Account account = accountRepository.findByUserIdAndAccountNum(user.getUserId(), request.getWdAccountNum())
                .orElseThrow(() -> new IllegalArgumentException("해당 계좌가 존재하지 않거나 권한이 없습니다."));
        log.info("✅ 계좌 조회 성공: accountId={}, 현재잔액={:,}원", account.getAccountId(), account.getBalanceAmt().intValue());
        
        // 3. 잔액 확인
        if (account.getBalanceAmt().compareTo(request.getTranAmt()) < 0) {
            log.error("❌ 잔액 부족: 현재잔액={:,}원, 요청금액={:,}원", 
                     account.getBalanceAmt().intValue(), request.getTranAmt().intValue());
            throw new IllegalArgumentException("잔액이 부족합니다.");
        }
        log.info("✅ 잔액 확인 통과: 현재잔액={:,}원, 출금금액={:,}원", 
                 account.getBalanceAmt().intValue(), request.getTranAmt().intValue());
        
        // 4. 거래 고유번호는 자동 생성됨 (AUTO_INCREMENT)
        
        // 5. 거래 일시 생성
        String tranDtime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        
        // 6. 잔액 차감 후 새로운 잔액 계산
        BigDecimal newBalance = account.getBalanceAmt().subtract(request.getTranAmt());
        
        // 7. 계좌 잔액 업데이트
        int updatedRows = accountRepository.updateAccountBalance(account.getAccountId(), newBalance);
        if (updatedRows != 1) {
            log.error("❌ 계좌 잔액 업데이트 실패: accountId={}", account.getAccountId());
            throw new IllegalStateException("계좌 잔액 업데이트에 실패했습니다.");
        }
        log.info("✅ 계좌 잔액 업데이트 성공: 신잔액={:,}원", newBalance.intValue());
        
        // 8. 거래 내역 저장
        Transaction transaction = createTransaction(account, request, newBalance, tranDtime);
        Transaction savedTransaction = transactionRepository.save(transaction);
        log.info("✅ 거래내역 저장 성공: tranId={}", savedTransaction.getTranId());
        
        log.info("🎉 [HANA-BACKEND] 출금 처리 완료");
        log.info("  ├─ 거래ID: {}", savedTransaction.getTranId());
        log.info("  ├─ 출금계좌: {}", account.getAccountNum());
        log.info("  ├─ 출금금액: {:,}원", request.getTranAmt().intValue());
        log.info("  └─ 출금후잔액: {:,}원", newBalance.intValue());
        
        return createWithdrawResponse(savedTransaction.getTranId().toString(), account, request, newBalance);
    }
    
    /**
     * 입금 처리
     */
    public DepositResponse processDeposit(DepositRequest request) {
        log.info("💰 [HANA-BACKEND] 입금 처리 시작");
        
        // 0. 입금 금액 검증
        if (request.getTranAmt() == null || request.getTranAmt().compareTo(BigDecimal.ZERO) <= 0) {
            log.error("❌ 입금 금액 검증 실패: {}", request.getTranAmt());
            throw new IllegalArgumentException("입금 금액은 0원보다 커야 합니다.");
        }
        log.info("✅ 입금 금액 검증 통과: {:,}원", request.getTranAmt().intValue());
        
        // 1. 계좌 조회 (reqClientNum으로 바로 계좌 찾기)
        Account account = accountRepository.findByAccountNum(request.getReqClientNum())
                .orElseThrow(() -> new IllegalArgumentException("해당 계좌가 존재하지 않습니다."));
        log.info("✅ 계좌 조회 성공: accountId={}, 현재잔액={:,}원", account.getAccountId(), account.getBalanceAmt().intValue());
        
        // 2. 거래 고유번호는 자동 생성됨 (AUTO_INCREMENT)
        
        // 3. 거래 일시 사용 (요청에 있는 tranDtime 사용)
        String tranDtime = request.getTranDtime();
        
        // 4. 잔액 증가 후 새로운 잔액 계산
        BigDecimal newBalance = account.getBalanceAmt().add(request.getTranAmt());
        
        // 5. 계좌 잔액 업데이트
        int updatedRows = accountRepository.updateAccountBalance(account.getAccountId(), newBalance);
        if (updatedRows != 1) {
            log.error("❌ 계좌 잔액 업데이트 실패: accountId={}", account.getAccountId());
            throw new IllegalStateException("계좌 잔액 업데이트에 실패했습니다.");
        }
        log.info("✅ 계좌 잔액 업데이트 성공: 신잔액={:,}원", newBalance.intValue());
        
        // 6. 거래 내역 저장
        Transaction transaction = createDepositTransaction(account, request, newBalance, tranDtime);
        Transaction savedTransaction = transactionRepository.save(transaction);
        log.info("✅ 거래내역 저장 성공: tranId={}", savedTransaction.getTranId());
        
        log.info("🎉 [HANA-BACKEND] 입금 처리 완료");
        log.info("  ├─ 거래ID: {}", savedTransaction.getTranId());
        log.info("  ├─ 입금계좌: {}", account.getAccountNum());
        log.info("  ├─ 입금금액: {:,}원", request.getTranAmt().intValue());
        log.info("  └─ 입금후잔액: {:,}원", newBalance.intValue());
        
        return createDepositResponse(savedTransaction.getTranId().toString(), account, request, newBalance);
    }

    // TransactionService.java에 추가할 메서드
    /**
     * 거래내역 조회
     */
    public TransactionHistoryResponse getTransactionHistory(TransactionHistoryRequest request) {
        log.info("거래내역 조회 요청 - CI: {}, 계좌번호: {}, 조회타입: {}, 정렬: {}",
                request.getUserCi(), request.getAccountNum(), request.getInquiryType(), request.getSortOrder());

        // 1. CI로 사용자 조회
        User user = userRepository.findByUserCi(request.getUserCi())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        // 2. 사용자 ID와 계좌번호로 계좌 조회
        Account account = accountRepository.findByUserIdAndAccountNum(user.getUserId(), request.getAccountNum())
                .orElseThrow(() -> new IllegalArgumentException("해당 계좌가 존재하지 않습니다."));

        // 3. 거래내역 조회
        List<Transaction> transactions = getTransactionsByInquiryType(account.getAccountId(), request.getInquiryType());

        // 4. 정렬 처리
        transactions = sortTransactions(transactions, request.getSortOrder());

        // 5. 응답 생성
        List<TransactionHistoryItem> historyItems = transactions.stream()
                .map(this::convertToHistoryItem)
                .collect(Collectors.toList());

        return TransactionHistoryResponse.builder()
                .balanceAmt(account.getBalanceAmt().toString())
                .pageRecordCnt(String.valueOf(historyItems.size()))
                .nextPageYn("N")
                .beforeInquiryTraceInfo(generateTraceInfo())
                .resList(historyItems)
                .build();
    }

    private List<Transaction> getTransactionsByInquiryType(Long accountId, String inquiryType) {
        switch (inquiryType.toUpperCase()) {
            case "I": // 입금만
                return transactionRepository.findByAccountIdAndInoutType(accountId, "DP");
            case "O": // 출금만
                return transactionRepository.findByAccountIdAndInoutType(accountId, "WD");
            case "A": // 모두
            default:
                return transactionRepository.findByAccountId(accountId);
        }
    }

    private List<Transaction> sortTransactions(List<Transaction> transactions, String sortOrder) {
        if ("A".equals(sortOrder.toUpperCase())) {
            // 오름차순 (날짜 오래된 순)
            return transactions.stream()
                    .sorted(Comparator.comparing(Transaction::getTranDate)
                            .thenComparing(Transaction::getTranTime))
                    .collect(Collectors.toList());
        } else {
            // 내림차순 (날짜 최신 순) - 기본값
            return transactions.stream()
                    .sorted(Comparator.comparing(Transaction::getTranDate)
                            .thenComparing(Transaction::getTranTime)
                            .reversed())
                    .collect(Collectors.toList());
        }
    }

    private TransactionHistoryItem convertToHistoryItem(Transaction transaction) {
        return TransactionHistoryItem.builder()
                .tranDate(transaction.getTranDate().format(DateTimeFormatter.ofPattern("yyyyMMdd")))
                .tranTime(transaction.getTranTime())
                .inoutType(mapInoutType(transaction.getInoutType()))
                .tranType(mapTranType(transaction.getTranType()))
                .printedContent(transaction.getPrintContent())
                .tranAmt(transaction.getTranAmt().toString())
                .afterBalanceAmt(transaction.getAfterBalanceAmt().toString())
                .branchName(transaction.getBranchName())
                .build();
    }

    private String mapInoutType(String inoutType) {
        switch (inoutType) {
            case "DP":
                return "입금";
            case "WD":
                return "출금";
            default:
                return "기타";
        }
    }

    private String mapTranType(String tranType) {
        switch (tranType) {
            case "CASH":
                return "현금";
            case "CARD":
                return "카드";
            case "TRANSFER":
                return "이체";
            default:
                return "온라인";
        }
    }

    private String generateTraceInfo() {
        return "1T" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
    }
    
    /**
     * 거래 고유번호는 AUTO_INCREMENT로 자동 생성됨
     */
    
    /**
     * 트랜잭션 엔티티 생성
     */
    private Transaction createTransaction(Account account, WithdrawRequest request, 
                                       BigDecimal newBalance, String tranDtime) {
        LocalDateTime now = LocalDateTime.now();
        
        return Transaction.builder()
                .accountId(account.getAccountId())
                .tranDate(now.toLocalDate())
                .tranTime(now.format(DateTimeFormatter.ofPattern("HHmmss")))
                .inoutType("WD")
                .tranType("ONLINE_WD")
                .printContent(request.getDpsPrintContent())
                .tranAmt(request.getTranAmt())
                .afterBalanceAmt(newBalance)
                .branchName("HANA_ONLINE")
                .wdBankCodeStd(account.getBankCodeStd())
                .wdAccountNum(request.getWdAccountNum())
                .reqClientName(request.getReqClientName())
                .build();
    }
    
    /**
     * 입금 트랜잭션 엔티티 생성
     */
    private Transaction createDepositTransaction(Account account, DepositRequest request, 
                                                BigDecimal newBalance, String tranDtime) {
        LocalDateTime now = LocalDateTime.now();
        
        return Transaction.builder()
                .accountId(account.getAccountId())
                .tranDate(now.toLocalDate())
                .tranTime(now.format(DateTimeFormatter.ofPattern("HHmmss")))
                .inoutType("DP")
                .tranType("ONLINE_DP")
                .printContent(request.getPrintContent())
                .tranAmt(request.getTranAmt())
                .afterBalanceAmt(newBalance)
                .branchName("HANA_ONLINE")
                .wdBankCodeStd(account.getBankCodeStd())
                .wdAccountNum(request.getReqClientNum())
                .reqClientName(request.getAccountHolderName())
                .build();
    }
    
    /**
     * 출금 응답 생성
     */
    private WithdrawResponse createWithdrawResponse(String tranId, Account account, 
                                                  WithdrawRequest request, BigDecimal newBalance) {
        return WithdrawResponse.builder()
                .bankTranId(tranId)
                .dpsPrintContent(request.getDpsPrintContent())
                .accountNum(account.getAccountNum())
                .accountAlias(account.getProductName())
                .bankCodeStd(account.getBankCodeStd())
                .bankCodeSub(account.getAccountLocalCode())
                .bankName(mapBankCodeToName(account.getBankCodeStd()))
                .accountNumMasked(account.getAccountNumMasked())
                .printContent(request.getDpsPrintContent())
                .accountHolderName(request.getReqClientName())
                .tranAmt(request.getTranAmt())
                .wdLimitRemainAmt(newBalance)
                .build();
    }
    
    /**
     * 입금 응답 생성
     */
    private DepositResponse createDepositResponse(String tranId, Account account, 
                                                  DepositRequest request, BigDecimal newBalance) {
        return DepositResponse.builder()
                .tranNo(request.getTranNo())
                .bankTranId(tranId)
                .bankTranDate(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")))
                .bankCodeTran(account.getBankCodeStd())
                .bankRspCode("000")
                .bankRspMessage("")
                .bankName(mapBankCodeToName(account.getBankCodeStd()))
                .accountNum(account.getAccountNum())
                .accountNumMasked(account.getAccountNumMasked())
                .printContent(request.getPrintContent())
                .accountHolderName(request.getAccountHolderName())
                .tranAmt(request.getTranAmt())
                .withdrawBankTranId(request.getBankTranId())
                .build();
    }
    
    /**
     * 은행 코드를 은행명으로 매핑
     */
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
} 