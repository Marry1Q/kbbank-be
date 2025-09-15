package com.kopo_team4.kbbank_backend.scheduler;

import com.kopo_team4.kbbank_backend.domain.account.entity.Account;
import com.kopo_team4.kbbank_backend.domain.account.repository.AccountRepository;
import com.kopo_team4.kbbank_backend.domain.autotransfer.entity.AutoTransfer;
import com.kopo_team4.kbbank_backend.domain.autotransfer.repository.AutoTransferRepository;
import com.kopo_team4.kbbank_backend.domain.autotransfer.service.InstallmentManager;
import com.kopo_team4.kbbank_backend.domain.autotransfer.service.ScheduleParser;
import com.kopo_team4.kbbank_backend.domain.transaction.entity.Transaction;
import com.kopo_team4.kbbank_backend.domain.transaction.repository.TransactionRepository;
import com.kopo_team4.kbbank_backend.global.exception.InsufficientBalanceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
public class AutoTransferScheduler {
    
    private final AutoTransferRepository autoTransferRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final InstallmentManager installmentManager;
    private final ScheduleParser scheduleParser;
    private final AccountLockManager accountLockManager;
    
    @Scheduled(cron = "0 0 0 * * ?")
    public void executeScheduledTransfers() {
        LocalDate today = LocalDate.now();
        log.info("자동이체 스케줄러 시작 - 실행일: {}", today);
        
        try {
            List<AutoTransfer> scheduledTransfers = autoTransferRepository
                .findScheduledTransfers("ACTIVE", today);
            
            if (scheduledTransfers.isEmpty()) {
                log.info("실행 예정인 자동이체가 없습니다.");
                return;
            }
            
            Map<Long, List<AutoTransfer>> transfersByAccount = scheduledTransfers.stream()
                .collect(Collectors.groupingBy(AutoTransfer::getFromAccountId));
            
            transfersByAccount.forEach((accountId, transfers) -> {
                executeTransfersForAccount(accountId, transfers);
            });
            
        } catch (Exception e) {
            log.error("자동이체 스케줄러 실행 중 오류 발생", e);
        }
    }
    
    private void executeTransfersForAccount(Long accountId, List<AutoTransfer> transfers) {
        accountLockManager.executeWithAccountLock(accountId, () -> {
            transfers.forEach(this::executeSingleTransfer);
        });
    }
    
    private void executeSingleTransfer(AutoTransfer autoTransfer) {
        try {
            // 중복 처리 방지: 이미 이번 달에 처리된 회차인지 확인
            if (isAlreadyProcessedThisMonth(autoTransfer)) {
                log.info("이미 이번 달에 처리된 자동이체입니다. 건너뜀 - ID: {}", autoTransfer.getAutoTransferId());
                return;
            }
            
            Account account = accountRepository.findByIdWithLock(autoTransfer.getFromAccountId())
                .orElseThrow(() -> new IllegalArgumentException("계좌를 찾을 수 없습니다."));
            
            if (account.getBalanceAmt().compareTo(autoTransfer.getAmount()) < 0) {
                throw new InsufficientBalanceException("잔액이 부족합니다.");
            }
            
            Transaction successTransaction = createTransaction(autoTransfer, "AUTO_TRANSFER", "Plan1Q 자동이체");
            transactionRepository.save(successTransaction);
            
            // 회차 정보 업데이트
            installmentManager.updateInstallmentInfo(autoTransfer);
            
            autoTransfer.setLastExecutionDate(LocalDate.now());
            autoTransfer.setLastExecutionStatus("SUCCESS");
            
            LocalDate nextDate = scheduleParser.calculateNextTransferDate(autoTransfer.getSchedule(), LocalDate.now());
            autoTransfer.setNextTransferDate(nextDate);
            
            autoTransferRepository.save(autoTransfer);
            
        } catch (Exception e) {
            autoTransfer.setLastExecutionDate(LocalDate.now());
            autoTransfer.setLastExecutionStatus("FAILED");
            autoTransferRepository.save(autoTransfer);
        }
    }
    
    /**
     * 이번 달에 이미 처리된 회차인지 확인
     */
    private boolean isAlreadyProcessedThisMonth(AutoTransfer autoTransfer) {
        LocalDate now = LocalDate.now();
        LocalDate thisMonthStart = now.withDayOfMonth(1);
        LocalDate thisMonthEnd = now.withDayOfMonth(now.lengthOfMonth());
        
        // 이번 달에 성공한 실행 기록이 있는지 확인
        return autoTransfer.getLastExecutionDate() != null &&
               autoTransfer.getLastExecutionDate().isAfter(thisMonthStart.minusDays(1)) &&
               autoTransfer.getLastExecutionDate().isBefore(thisMonthEnd.plusDays(1)) &&
               "SUCCESS".equals(autoTransfer.getLastExecutionStatus());
    }
    
    private Transaction createTransaction(AutoTransfer autoTransfer, String tranType, String memo) {
        return Transaction.builder()
            .accountId(autoTransfer.getFromAccountId())
            .tranDate(LocalDate.now())
            .tranTime(LocalTime.now().format(DateTimeFormatter.ofPattern("HHmmss")))
            .inoutType("WD")
            .tranType(tranType)
            .printContent(memo)
            .tranAmt(autoTransfer.getAmount())
            .wdBankCodeStd("081")
            .wdAccountNum(autoTransfer.getFromAccountId().toString())
            .reqClientName("Plan1Q")
            .build();
    }
}
